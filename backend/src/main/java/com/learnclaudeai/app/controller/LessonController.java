package com.learnclaudeai.app.controller;

import com.learnclaudeai.app.dto.LessonDto;
import com.learnclaudeai.app.entity.Lesson;
import com.learnclaudeai.app.entity.User;
import com.learnclaudeai.app.repository.LessonRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/lessons")
public class LessonController {

    private final LessonRepository lessonRepository;

    public LessonController(LessonRepository lessonRepository) {
        this.lessonRepository = lessonRepository;
    }

    @GetMapping
    @Cacheable("lessons")
    public List<LessonDto> getAllLessons() {
        return lessonRepository.findAllByOrderByNumberAsc().stream()
                .map(LessonDto::from)
                .toList();
    }

    @GetMapping("/{id}")
    @Cacheable(value = "lesson", key = "#id")
    public ResponseEntity<?> getLesson(@PathVariable Long id, @AuthenticationPrincipal User user) {
        Lesson lesson = lessonRepository.findById(id).orElse(null);
        if (lesson == null) {
            return ResponseEntity.notFound().build();
        }

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (lesson.getPlan() == User.Plan.PRO && user.getPlan() == User.Plan.FREE) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Upgrade to Pro to access this lesson"));
        }

        return ResponseEntity.ok(LessonDto.from(lesson));
    }
}
