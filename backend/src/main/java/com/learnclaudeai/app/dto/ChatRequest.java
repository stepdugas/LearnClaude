package com.learnclaudeai.app.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ChatRequest(
        @NotBlank @Size(max = 4000) String message,
        @Size(max = 50) List<@Valid ChatMessage> conversationHistory
) {
    public record ChatMessage(
            @NotBlank @Pattern(regexp = "^(user|assistant)$") String role,
            @NotBlank @Size(max = 8000) String content
    ) {}
}
