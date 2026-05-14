package com.learnclaudeai.app.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final String apiKey;
    private final String fromEmail;
    private final String siteUrl;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    public EmailService(@Value("${resend.api.key}") String apiKey,
                        @Value("${resend.from.email}") String fromEmail,
                        @Value("${site.url}") String siteUrl) {
        this.apiKey = apiKey;
        this.fromEmail = fromEmail;
        this.siteUrl = siteUrl;
    }

    public boolean isConfigured() {
        return apiKey != null && !apiKey.isBlank();
    }

    public void sendNewLessonNotification(String toEmail, String lessonTitle, String lessonSlug) {
        if (!isConfigured()) {
            log.info("Email not configured. Would notify {} about: {}", toEmail, lessonTitle);
            return;
        }

        executor.execute(() -> {
            try {
                String html = """
                    <div style="font-family: Inter, system-ui, sans-serif; max-width: 500px; margin: 0 auto; padding: 32px;">
                        <h2 style="color: #1A1A1A; font-size: 22px; margin-bottom: 8px;">New Lesson Available</h2>
                        <p style="color: #524E49; font-size: 15px; line-height: 1.6; margin-bottom: 24px;">
                            A new lesson just dropped on LearnClaude.ai:
                        </p>
                        <div style="background: #FAF9F5; border: 1px solid #F0EDE6; border-radius: 12px; padding: 20px; margin-bottom: 24px;">
                            <h3 style="color: #DA7756; font-size: 18px; margin: 0 0 4px 0;">%s</h3>
                        </div>
                        <a href="%s/learn/%s"
                           style="display: inline-block; background: #DA7756; color: white; font-weight: 600;
                                  padding: 12px 28px; border-radius: 8px; text-decoration: none; font-size: 15px;">
                            Start Learning
                        </a>
                        <p style="color: #999; font-size: 12px; margin-top: 32px;">
                            You're receiving this because you opted in to new lesson notifications on LearnClaude.ai.
                        </p>
                    </div>
                    """.formatted(lessonTitle, siteUrl, lessonSlug);

                String json = """
                    {"from":"%s","to":["%s"],"subject":"New Lesson: %s — LearnClaude.ai","html":"%s"}
                    """.formatted(
                        fromEmail,
                        toEmail,
                        lessonTitle,
                        html.replace("\"", "\\\"").replace("\n", "")
                );

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://api.resend.com/emails"))
                        .header("Authorization", "Bearer " + apiKey)
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() >= 200 && response.statusCode() < 300) {
                    log.info("Email sent to {} for lesson: {}", toEmail, lessonTitle);
                } else {
                    log.warn("Email send failed for {}: {} {}", toEmail, response.statusCode(), response.body());
                }
            } catch (Exception e) {
                log.error("Failed to send email to {}: {}", toEmail, e.getMessage());
            }
        });
    }
}
