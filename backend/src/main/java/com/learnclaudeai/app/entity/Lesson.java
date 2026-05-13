package com.learnclaudeai.app.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "lessons")
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Integer number;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(nullable = false)
    private Integer tier;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private User.Plan plan;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(name = "youtube_search_query", nullable = false)
    private String youtubeSearchQuery;

    @Column(nullable = false, length = 500)
    private String challenge;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getNumber() { return number; }
    public void setNumber(Integer number) { this.number = number; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public Integer getTier() { return tier; }
    public void setTier(Integer tier) { this.tier = tier; }

    public User.Plan getPlan() { return plan; }
    public void setPlan(User.Plan plan) { this.plan = plan; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getYoutubeSearchQuery() { return youtubeSearchQuery; }
    public void setYoutubeSearchQuery(String youtubeSearchQuery) { this.youtubeSearchQuery = youtubeSearchQuery; }

    public String getChallenge() { return challenge; }
    public void setChallenge(String challenge) { this.challenge = challenge; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
