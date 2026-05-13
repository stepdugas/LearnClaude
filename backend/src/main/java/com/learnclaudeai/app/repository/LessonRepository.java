package com.learnclaudeai.app.repository;

import com.learnclaudeai.app.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findAllByOrderByNumberAsc();
    boolean existsByNumber(Integer number);
    Optional<Lesson> findByNumber(Integer number);
    Optional<Lesson> findBySlug(String slug);

    @Query("SELECT COALESCE(MAX(l.number), 0) FROM Lesson l")
    int findMaxNumber();
}
