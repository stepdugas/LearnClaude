package com.learnclaudeai.app.repository;

import com.learnclaudeai.app.entity.CertificateRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CertificateRecordRepository extends JpaRepository<CertificateRecord, Long> {
    Optional<CertificateRecord> findByUserId(Long userId);
    Optional<CertificateRecord> findByCertificateId(String certificateId);
}
