package com.learnclaudeai.app.dto;

import com.learnclaudeai.app.entity.Lesson;

import java.time.Instant;

public record LessonDto(
        Long id,
        Integer number,
        String title,
        String slug,
        Integer tier,
        String plan,
        String description,
        String youtubeSearchQuery,
        String challenge,
        String deliverable,
        Instant createdAt
) {
    public static LessonDto from(Lesson lesson) {
        return new LessonDto(
                lesson.getId(),
                lesson.getNumber(),
                lesson.getTitle(),
                lesson.getSlug(),
                lesson.getTier(),
                lesson.getPlan().name(),
                lesson.getDescription(),
                lesson.getYoutubeSearchQuery(),
                lesson.getChallenge(),
                lesson.getDeliverable(),
                lesson.getCreatedAt()
        );
    }
}
