package com.learnclaudeai.app.controller;

import com.learnclaudeai.app.dto.LessonDto;
import com.learnclaudeai.app.repository.LessonRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/lessons")
public class PublicLessonController {

    private final LessonRepository lessonRepository;

    public PublicLessonController(LessonRepository lessonRepository) {
        this.lessonRepository = lessonRepository;
    }

    @GetMapping
    @Cacheable("lessons")
    public List<LessonDto> getAllLessons() {
        return lessonRepository.findAllByOrderByNumberAsc().stream()
                .map(LessonDto::from)
                .toList();
    }

    @GetMapping("/{slug}")
    @Cacheable(value = "lessonBySlug", key = "#slug")
    public ResponseEntity<LessonDto> getLessonBySlug(@PathVariable String slug) {
        return lessonRepository.findBySlug(slug)
                .map(lesson -> ResponseEntity.ok(LessonDto.from(lesson)))
                .orElse(ResponseEntity.notFound().build());
    }
}
