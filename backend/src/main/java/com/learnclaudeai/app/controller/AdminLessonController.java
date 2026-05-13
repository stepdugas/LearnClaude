package com.learnclaudeai.app.controller;

import com.learnclaudeai.app.dto.AdminLessonRequest;
import com.learnclaudeai.app.dto.LessonDto;
import com.learnclaudeai.app.entity.Lesson;
import com.learnclaudeai.app.entity.User;
import com.learnclaudeai.app.repository.LessonRepository;
import com.learnclaudeai.app.repository.UserRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/lessons")
public class AdminLessonController {

    private static final Logger log = LoggerFactory.getLogger(AdminLessonController.class);

    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;

    public AdminLessonController(LessonRepository lessonRepository, UserRepository userRepository) {
        this.lessonRepository = lessonRepository;
        this.userRepository = userRepository;
    }

    private ResponseEntity<?> requireAdmin(User user) {
        if (user == null || !user.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Admin access required"));
        }
        return null;
    }

    @GetMapping
    public ResponseEntity<?> listLessons(@AuthenticationPrincipal User user) {
        ResponseEntity<?> check = requireAdmin(user);
        if (check != null) return check;

        List<LessonDto> lessons = lessonRepository.findAllByOrderByNumberAsc().stream()
                .map(LessonDto::from)
                .toList();
        return ResponseEntity.ok(lessons);
    }

    @PostMapping
    @Transactional
    @CacheEvict(value = {"lessons", "lesson"}, allEntries = true)
    public ResponseEntity<?> createLesson(@Valid @RequestBody AdminLessonRequest request,
                                          @AuthenticationPrincipal User user) {
        ResponseEntity<?> check = requireAdmin(user);
        if (check != null) return check;

        if (lessonRepository.existsByNumber(request.number())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Lesson number " + request.number() + " already exists"));
        }

        Lesson lesson = applyRequest(new Lesson(), request);
        lesson = lessonRepository.save(lesson);

        log.info("Admin {} created lesson #{}: {}", user.getEmail(), lesson.getNumber(), lesson.getTitle());

        // Find users to notify
        List<User> subscribedUsers = userRepository.findByNotifyNewLessonsTrue();
        log.info("New lesson created. {} users subscribed for notifications.", subscribedUsers.size());
        // TODO: Hook up email service (Resend, SendGrid, etc.) here
        // For now, log the emails that should be notified
        subscribedUsers.forEach(u -> log.info("  Notify: {}", u.getEmail()));

        return ResponseEntity.status(HttpStatus.CREATED).body(LessonDto.from(lesson));
    }

    @PutMapping("/{id}")
    @Transactional
    @CacheEvict(value = {"lessons", "lesson"}, allEntries = true)
    public ResponseEntity<?> updateLesson(@PathVariable Long id,
                                          @Valid @RequestBody AdminLessonRequest request,
                                          @AuthenticationPrincipal User user) {
        ResponseEntity<?> check = requireAdmin(user);
        if (check != null) return check;

        Lesson lesson = lessonRepository.findById(id).orElse(null);
        if (lesson == null) return ResponseEntity.notFound().build();

        // If number changed, check it's not taken by another lesson
        if (!lesson.getNumber().equals(request.number()) && lessonRepository.existsByNumber(request.number())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Lesson number " + request.number() + " already exists"));
        }

        lesson = applyRequest(lesson, request);
        lesson = lessonRepository.save(lesson);

        log.info("Admin {} updated lesson #{}: {}", user.getEmail(), lesson.getNumber(), lesson.getTitle());
        return ResponseEntity.ok(LessonDto.from(lesson));
    }

    @DeleteMapping("/{id}")
    @Transactional
    @CacheEvict(value = {"lessons", "lesson"}, allEntries = true)
    public ResponseEntity<?> deleteLesson(@PathVariable Long id, @AuthenticationPrincipal User user) {
        ResponseEntity<?> check = requireAdmin(user);
        if (check != null) return check;

        Lesson lesson = lessonRepository.findById(id).orElse(null);
        if (lesson == null) return ResponseEntity.notFound().build();

        log.info("Admin {} deleted lesson #{}: {}", user.getEmail(), lesson.getNumber(), lesson.getTitle());
        lessonRepository.delete(lesson);
        return ResponseEntity.ok(Map.of("message", "Lesson deleted"));
    }

    @GetMapping("/next-number")
    public ResponseEntity<?> getNextNumber(@AuthenticationPrincipal User user) {
        ResponseEntity<?> check = requireAdmin(user);
        if (check != null) return check;

        return ResponseEntity.ok(Map.of("nextNumber", lessonRepository.findMaxNumber() + 1));
    }

    private Lesson applyRequest(Lesson lesson, AdminLessonRequest request) {
        lesson.setNumber(request.number());
        lesson.setTitle(request.title());
        lesson.setTier(request.tier());
        lesson.setPlan(User.Plan.valueOf(request.plan()));
        lesson.setDescription(request.description());
        lesson.setYoutubeSearchQuery(request.youtubeSearchQuery());
        lesson.setChallenge(request.challenge());
        return lesson;
    }
}
