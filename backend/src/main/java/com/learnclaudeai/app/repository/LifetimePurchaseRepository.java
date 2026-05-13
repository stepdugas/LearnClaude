package com.learnclaudeai.app.repository;

import com.learnclaudeai.app.entity.LifetimePurchase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LifetimePurchaseRepository extends JpaRepository<LifetimePurchase, Long> {
}
