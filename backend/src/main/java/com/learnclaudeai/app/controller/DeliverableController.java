package com.learnclaudeai.app.controller;

import com.anthropic.client.AnthropicClient;
import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import com.anthropic.models.messages.*;
import com.learnclaudeai.app.entity.DeliverableSubmission;
import com.learnclaudeai.app.entity.Lesson;
import com.learnclaudeai.app.entity.User;
import com.learnclaudeai.app.repository.DeliverableSubmissionRepository;
import com.learnclaudeai.app.repository.LessonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/deliverables")
public class DeliverableController {

    private static final Logger log = LoggerFactory.getLogger(DeliverableController.class);

    private final DeliverableSubmissionRepository submissionRepository;
    private final LessonRepository lessonRepository;
    private final AnthropicClient anthropicClient;

    public DeliverableController(DeliverableSubmissionRepository submissionRepository,
                                 LessonRepository lessonRepository,
                                 @Value("${anthropic.api.key}") String apiKey) {
        this.submissionRepository = submissionRepository;
        this.lessonRepository = lessonRepository;
        this.anthropicClient = (apiKey != null && !apiKey.isBlank())
                ? AnthropicOkHttpClient.builder().apiKey(apiKey).build()
                : null;
    }

    @PostMapping("/{lessonId}")
    public ResponseEntity<?> submitDeliverable(@PathVariable Long lessonId,
                                               @RequestBody Map<String, String> body,
                                               @AuthenticationPrincipal User user) {
        String submission = body.get("submission");
        if (submission == null || submission.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Submission text is required"));
        }
        if (submission.length() > 5000) {
            return ResponseEntity.badRequest().body(Map.of("error", "Submission must be under 5000 characters"));
        }

        Lesson lesson = lessonRepository.findById(lessonId).orElse(null);
        if (lesson == null) {
            return ResponseEntity.notFound().build();
        }

        if (anthropicClient == null) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("error", "AI reviewer is not configured"));
        }

        // Build the review prompt
        String systemPrompt = String.format(
                "You are an encouraging but honest instructor reviewing a student's hands-on work for a lesson called '%s' on LearnClaude.ai. " +
                "The expected deliverable was: %s. Review their submission, tell them specifically what they did well, what could be better, " +
                "and confirm whether they have successfully completed the deliverable. End your response with exactly one of these on its own line: PASSED or NOT_YET_PASSED. " +
                "Be warm, specific, and under 120 words.",
                lesson.getTitle(),
                lesson.getDeliverable() != null ? lesson.getDeliverable() : lesson.getChallenge()
        );

        try {
            // Call Claude API (non-streaming) to review the submission
            Message response = anthropicClient.messages().create(MessageCreateParams.builder()
                    .model("claude-sonnet-4-20250514")
                    .maxTokens(512)
                    .system(systemPrompt)
                    .addUserMessage(submission)
                    .build());

            // Extract the text content from the response
            StringBuilder feedbackBuilder = new StringBuilder();
            for (ContentBlock block : response.content()) {
                if (block.isText()) {
                    feedbackBuilder.append(block.asText().text());
                }
            }
            String feedback = feedbackBuilder.toString();

            // Parse passed status: check if response contains "PASSED" but not "NOT_YET_PASSED"
            boolean passed = feedback.contains("PASSED") && !feedback.contains("NOT_YET_PASSED");

            // Save or update the submission
            DeliverableSubmission sub = submissionRepository
                    .findByUserIdAndLessonId(user.getId(), lessonId)
                    .orElse(new DeliverableSubmission());

            sub.setUserId(user.getId());
            sub.setLessonId(lessonId);
            sub.setSubmissionText(submission);
            sub.setAiFeedback(feedback);
            sub.setFeedbackGeneratedAt(Instant.now());
            submissionRepository.save(sub);

            log.info("User {} submitted deliverable for lesson {}: passed={}", user.getEmail(), lessonId, passed);

            return ResponseEntity.ok(Map.of("feedback", feedback, "passed", passed));

        } catch (Exception e) {
            log.error("Failed to review deliverable for lesson {}: {}", lessonId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to review your submission. Please try again."));
        }
    }

    @GetMapping("/{lessonId}")
    public ResponseEntity<?> getSubmission(@PathVariable Long lessonId,
                                           @AuthenticationPrincipal User user) {
        return submissionRepository.findByUserIdAndLessonId(user.getId(), lessonId)
                .map(sub -> ResponseEntity.ok(Map.of(
                        "submission", sub.getSubmissionText(),
                        "feedback", sub.getAiFeedback() != null ? sub.getAiFeedback() : "",
                        "submittedAt", sub.getSubmittedAt().toString()
                )))
                .orElse(ResponseEntity.ok(null));
    }

    @GetMapping
    public List<Long> getSubmittedLessonIds(@AuthenticationPrincipal User user) {
        return submissionRepository.findByUserId(user.getId()).stream()
                .map(DeliverableSubmission::getLessonId)
                .toList();
    }
}
