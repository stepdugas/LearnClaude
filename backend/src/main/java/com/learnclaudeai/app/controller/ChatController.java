package com.learnclaudeai.app.controller;

import com.anthropic.client.AnthropicClient;
import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import com.anthropic.core.http.StreamResponse;
import com.anthropic.models.messages.*;
import com.learnclaudeai.app.dto.ChatRequest;
import com.learnclaudeai.app.entity.Lesson;
import com.learnclaudeai.app.entity.User;
import com.learnclaudeai.app.repository.LessonRepository;
import com.learnclaudeai.app.security.RateLimiter;
import jakarta.annotation.PreDestroy;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private static final Logger log = LoggerFactory.getLogger(ChatController.class);

    private final LessonRepository lessonRepository;
    private final AnthropicClient anthropicClient;
    private final RateLimiter rateLimiter;
    private final int freeDailyLimit;
    private final int proDailyLimit;

    // Virtual threads (Java 21) — no thread pool sizing worries,
    // each stream gets its own lightweight thread
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    public ChatController(LessonRepository lessonRepository,
                          RateLimiter rateLimiter,
                          @Value("${anthropic.api.key}") String apiKey,
                          @Value("${chat.daily-limit.free}") int freeDailyLimit,
                          @Value("${chat.daily-limit.pro}") int proDailyLimit) {
        this.lessonRepository = lessonRepository;
        this.rateLimiter = rateLimiter;
        this.freeDailyLimit = freeDailyLimit;
        this.proDailyLimit = proDailyLimit;
        this.anthropicClient = (apiKey != null && !apiKey.isBlank())
                ? AnthropicOkHttpClient.builder().apiKey(apiKey).build()
                : null;
    }

    @PreDestroy
    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    @PostMapping(value = "/{lessonId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Object chat(@PathVariable Long lessonId,
                       @Valid @RequestBody ChatRequest request,
                       @AuthenticationPrincipal User user) {

        // Rate limit: per-user cooldown (2s between messages)
        if (rateLimiter.isChatThrottled(user.getId())) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("error", "Please wait a moment between messages"));
        }

        // Daily message cap
        int dailyLimit = user.getPlan() == User.Plan.PRO ? proDailyLimit : freeDailyLimit;
        int remaining = rateLimiter.checkChatDailyLimit(user.getId(), dailyLimit);
        if (remaining < 0) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("error", "Daily message limit reached (" + dailyLimit + "/day). Resets tomorrow.",
                                 "limit", dailyLimit));
        }

        Lesson lesson = lessonRepository.findById(lessonId).orElse(null);
        if (lesson == null) {
            return ResponseEntity.notFound().build();
        }

        if (lesson.getPlan() == User.Plan.PRO && user.getPlan() == User.Plan.FREE) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Upgrade to Pro to use the AI tutor on this lesson"));
        }

        if (anthropicClient == null) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("error", "AI tutor is not configured"));
        }

        String systemPrompt = buildSystemPrompt(lesson);

        List<MessageParam> messages = new ArrayList<>();
        if (request.conversationHistory() != null) {
            for (ChatRequest.ChatMessage msg : request.conversationHistory()) {
                if (!"user".equals(msg.role()) && !"assistant".equals(msg.role())) continue;
                messages.add(MessageParam.builder()
                        .role("user".equals(msg.role()) ? MessageParam.Role.USER : MessageParam.Role.ASSISTANT)
                        .content(msg.content())
                        .build());
            }
        }
        messages.add(MessageParam.builder()
                .role(MessageParam.Role.USER)
                .content(request.message())
                .build());

        SseEmitter emitter = new SseEmitter(60_000L);
        AtomicBoolean cancelled = new AtomicBoolean(false);

        emitter.onTimeout(() -> cancelled.set(true));
        emitter.onCompletion(() -> cancelled.set(true));
        emitter.onError(e -> cancelled.set(true));

        List<MessageParam> finalMessages = messages;
        executor.execute(() -> {
            try (StreamResponse<RawMessageStreamEvent> stream = anthropicClient.messages()
                    .createStreaming(MessageCreateParams.builder()
                            .model("claude-sonnet-4-20250514")
                            .maxTokens(1024)
                            .system(systemPrompt)
                            .messages(finalMessages)
                            .build())) {

                stream.stream().takeWhile(event -> !cancelled.get()).forEach(event -> {
                    if (cancelled.get()) return;

                    if (event.isContentBlockDelta()) {
                        RawContentBlockDeltaEvent deltaEvent = event.asContentBlockDelta();
                        RawContentBlockDelta delta = deltaEvent.delta();
                        if (delta.isText()) {
                            try {
                                emitter.send(SseEmitter.event()
                                        .data(delta.asText().text()));
                            } catch (Exception e) {
                                log.debug("Client disconnected during streaming", e);
                                cancelled.set(true);
                            }
                        }
                    } else if (event.isStop()) {
                        try {
                            emitter.send(SseEmitter.event()
                                    .name("done")
                                    .data("[DONE]"));
                        } catch (Exception e) {
                            log.debug("Client disconnected at stream end", e);
                            cancelled.set(true);
                        }
                    }
                });

                if (!cancelled.get()) {
                    emitter.complete();
                }
            } catch (Exception e) {
                log.warn("Chat streaming error for lesson {}: {}", lessonId, e.getMessage());
                if (!cancelled.get()) {
                    try {
                        emitter.send(SseEmitter.event()
                                .name("error")
                                .data("Tutor is unavailable right now, try again in a moment"));
                        emitter.complete();
                    } catch (Exception ignored) {}
                }
            }
        });

        return emitter;
    }

    private String buildSystemPrompt(Lesson lesson) {
        return """
                You are an expert Claude AI tutor teaching a lesson called '%s' on the LearnClaude.ai platform.

                Your job is to help the user understand and practice: %s

                The hands-on challenge for this lesson is: %s

                Teaching guidelines:
                - Be encouraging, concise, and practical
                - Use real examples specific to this lesson topic
                - If the user shares a prompt they wrote, give specific actionable feedback
                - If the user is stuck on the challenge, give hints not full answers
                - Keep responses under 150 words unless the user asks for more detail
                - Use a warm, friendly tone that matches the Claude brand
                - When the user completes the challenge successfully, congratulate them and suggest they mark the lesson complete
                """.formatted(lesson.getTitle(), lesson.getDescription(), lesson.getChallenge());
    }
}
