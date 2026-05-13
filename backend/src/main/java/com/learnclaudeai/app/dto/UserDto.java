package com.learnclaudeai.app.dto;

import com.learnclaudeai.app.entity.User;

public record UserDto(Long id, String email, String plan, String role, boolean notifyNewLessons) {

    public static UserDto from(User user) {
        return new UserDto(
                user.getId(),
                user.getEmail(),
                user.getPlan().name(),
                user.getRole().name(),
                user.isNotifyNewLessons()
        );
    }
}
