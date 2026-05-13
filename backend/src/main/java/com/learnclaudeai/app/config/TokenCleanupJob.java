package com.learnclaudeai.app.config;

import com.learnclaudeai.app.repository.RefreshTokenRepository;
import com.learnclaudeai.app.security.RateLimiter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
public class TokenCleanupJob {

    private final RefreshTokenRepository refreshTokenRepository;
    private final RateLimiter rateLimiter;

    public TokenCleanupJob(RefreshTokenRepository refreshTokenRepository, RateLimiter rateLimiter) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.rateLimiter = rateLimiter;
    }

    @Scheduled(fixedRate = 3600000) // every hour
    @Transactional
    public void cleanupExpiredTokens() {
        refreshTokenRepository.deleteExpiredTokens(Instant.now());
    }

    @Scheduled(fixedRate = 300000) // every 5 minutes
    public void cleanupRateLimiter() {
        rateLimiter.cleanup();
    }
}
