package com.learnclaudeai.app.controller;

import com.learnclaudeai.app.entity.User;
import com.learnclaudeai.app.entity.UserProgress;
import com.learnclaudeai.app.repository.LessonRepository;
import com.learnclaudeai.app.repository.UserProgressRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/progress")
public class ProgressController {

    private final UserProgressRepository progressRepository;
    private final LessonRepository lessonRepository;

    public ProgressController(UserProgressRepository progressRepository, LessonRepository lessonRepository) {
        this.progressRepository = progressRepository;
        this.lessonRepository = lessonRepository;
    }

    @GetMapping
    public List<Long> getProgress(@AuthenticationPrincipal User user) {
        return progressRepository.findByUserId(user.getId()).stream()
                .map(UserProgress::getLessonId)
                .toList();
    }

    @Transactional
    @PostMapping("/{lessonId}")
    public ResponseEntity<?> markComplete(@PathVariable Long lessonId, @AuthenticationPrincipal User user) {
        if (!lessonRepository.existsById(lessonId)) {
            return ResponseEntity.notFound().build();
        }

        if (progressRepository.existsByUserIdAndLessonId(user.getId(), lessonId)) {
            return ResponseEntity.ok(Map.of("message", "Already completed"));
        }

        UserProgress progress = new UserProgress();
        progress.setUserId(user.getId());
        progress.setLessonId(lessonId);
        progressRepository.save(progress);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Lesson marked complete"));
    }
}
