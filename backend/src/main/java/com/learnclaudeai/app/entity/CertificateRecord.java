package com.learnclaudeai.app.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "certificate_records")
public class CertificateRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "certificate_id", nullable = false, unique = true)
    private String certificateId;

    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Column(name = "issued_at", nullable = false)
    private Instant issuedAt;

    @PrePersist
    protected void onCreate() {
        issuedAt = Instant.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getCertificateId() { return certificateId; }
    public void setCertificateId(String certificateId) { this.certificateId = certificateId; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public Instant getIssuedAt() { return issuedAt; }
}
