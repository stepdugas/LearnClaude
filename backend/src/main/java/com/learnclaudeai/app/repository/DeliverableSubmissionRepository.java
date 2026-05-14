package com.learnclaudeai.app.repository;

import com.learnclaudeai.app.entity.DeliverableSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeliverableSubmissionRepository extends JpaRepository<DeliverableSubmission, Long> {
    Optional<DeliverableSubmission> findByUserIdAndLessonId(Long userId, Long lessonId);
    List<DeliverableSubmission> findByUserId(Long userId);
    long countByUserId(Long userId);
}
