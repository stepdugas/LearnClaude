package com.learnclaudeai.app.dto;

import com.learnclaudeai.app.entity.User;

import java.time.Instant;

public record UserDto(
        Long id,
        String email,
        String plan,
        String role,
        boolean notifyNewLessons,
        String subscriptionStatus,
        Instant planExpiresAt
) {
    public static UserDto from(User user) {
        return new UserDto(
                user.getId(),
                user.getEmail(),
                user.getPlan().name(),
                user.getRole().name(),
                user.isNotifyNewLessons(),
                user.getSubscriptionStatus(),
                user.getPlanExpiresAt()
        );
    }
}
