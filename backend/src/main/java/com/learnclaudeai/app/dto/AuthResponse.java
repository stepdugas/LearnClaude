package com.learnclaudeai.app.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        UserDto user
) {}
