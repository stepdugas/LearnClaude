package com.learnclaudeai.app.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "lifetime_purchases")
public class LifetimePurchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "purchased_at", nullable = false)
    private Instant purchasedAt;

    @PrePersist
    protected void onCreate() {
        purchasedAt = Instant.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Instant getPurchasedAt() { return purchasedAt; }
}
