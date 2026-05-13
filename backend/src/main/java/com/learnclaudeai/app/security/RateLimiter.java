package com.learnclaudeai.app.security;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimiter {

    // Per-IP: key -> [count, windowStart]
    private final ConcurrentHashMap<String, long[]> ipWindows = new ConcurrentHashMap<>();
    // Per-user chat: userId -> [count, windowStart]
    private final ConcurrentHashMap<Long, long[]> chatDailyCount = new ConcurrentHashMap<>();
    // Per-user throttle: userId -> lastRequestTime
    private final ConcurrentHashMap<Long, Long> chatThrottle = new ConcurrentHashMap<>();

    private static final long ONE_MINUTE = 60_000L;
    private static final long ONE_DAY = 86_400_000L;
    private static final long CHAT_COOLDOWN_MS = 2_000L;

    /**
     * IP rate limit: max requests per minute per IP.
     */
    public boolean isIpRateLimited(String ip, int maxPerMinute) {
        long now = System.currentTimeMillis();
        long[] window = ipWindows.compute(ip, (k, v) -> {
            if (v == null || now - v[1] > ONE_MINUTE) {
                return new long[]{1, now};
            }
            v[0]++;
            return v;
        });
        return window[0] > maxPerMinute;
    }

    /**
     * Daily chat message cap per user. Returns remaining messages.
     */
    public int checkChatDailyLimit(Long userId, int dailyLimit) {
        long now = System.currentTimeMillis();
        long[] window = chatDailyCount.compute(userId, (k, v) -> {
            if (v == null || now - v[1] > ONE_DAY) {
                return new long[]{1, now};
            }
            v[0]++;
            return v;
        });
        return dailyLimit - (int) window[0] + 1;
    }

    /**
     * Per-user cooldown between chat messages (prevent rapid fire).
     */
    public boolean isChatThrottled(Long userId) {
        long now = System.currentTimeMillis();
        Long last = chatThrottle.put(userId, now);
        return last != null && (now - last) < CHAT_COOLDOWN_MS;
    }

    /**
     * Periodic cleanup of expired entries (call from scheduled job).
     */
    public void cleanup() {
        long now = System.currentTimeMillis();
        ipWindows.entrySet().removeIf(e -> now - e.getValue()[1] > ONE_MINUTE * 5);
        chatDailyCount.entrySet().removeIf(e -> now - e.getValue()[1] > ONE_DAY);
        chatThrottle.entrySet().removeIf(e -> now - e.getValue() > ONE_MINUTE);
    }
}
