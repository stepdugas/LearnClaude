package com.learnclaudeai.app.controller;

import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.learnclaudeai.app.entity.CertificateRecord;
import com.learnclaudeai.app.entity.User;
import com.learnclaudeai.app.repository.CertificateRecordRepository;
import com.learnclaudeai.app.repository.LessonRepository;
import com.learnclaudeai.app.repository.UserProgressRepository;
import com.learnclaudeai.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/certificate")
public class CertificateController {

    private final UserProgressRepository progressRepository;
    private final LessonRepository lessonRepository;
    private final CertificateRecordRepository certificateRepository;
    private final UserRepository userRepository;
    private final String siteUrl;

    // Colors
    private static final DeviceRgb CREAM = new DeviceRgb(250, 249, 245);
    private static final DeviceRgb CORAL = new DeviceRgb(218, 119, 86);
    private static final DeviceRgb CHARCOAL = new DeviceRgb(26, 26, 26);
    private static final DeviceRgb MUTED = new DeviceRgb(82, 78, 73);

    public CertificateController(UserProgressRepository progressRepository,
                                  LessonRepository lessonRepository,
                                  CertificateRecordRepository certificateRepository,
                                  UserRepository userRepository,
                                  @Value("${site.url}") String siteUrl) {
        this.progressRepository = progressRepository;
        this.lessonRepository = lessonRepository;
        this.certificateRepository = certificateRepository;
        this.userRepository = userRepository;
        this.siteUrl = siteUrl;
    }

    @GetMapping
    @Transactional
    public ResponseEntity<?> getCertificate(@AuthenticationPrincipal User authUser) {
        User user = userRepository.findById(authUser.getId()).orElse(null);
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        long totalLessons = lessonRepository.count();
        long completedLessons = progressRepository.countByUserId(user.getId());

        if (completedLessons < totalLessons) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "error", "Complete all " + totalLessons + " lessons to unlock your certificate",
                            "completed", completedLessons,
                            "total", totalLessons
                    ));
        }

        // Find or create certificate record
        CertificateRecord record = certificateRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    CertificateRecord newRecord = new CertificateRecord();
                    newRecord.setUserId(user.getId());
                    newRecord.setCertificateId(UUID.randomUUID().toString());
                    newRecord.setUserEmail(user.getEmail());
                    return certificateRepository.save(newRecord);
                });

        byte[] pdf = generatePdf(user.getEmail(), record.getCertificateId(), record.getIssuedAt());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=learnclaudeai-certificate.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/status")
    public ResponseEntity<?> getStatus(@AuthenticationPrincipal User authUser) {
        long totalLessons = lessonRepository.count();
        long completedLessons = progressRepository.countByUserId(authUser.getId());

        CertificateRecord record = certificateRepository.findByUserId(authUser.getId()).orElse(null);

        return ResponseEntity.ok(Map.of(
                "completed", completedLessons,
                "total", totalLessons,
                "eligible", completedLessons >= totalLessons,
                "certificateId", record != null ? record.getCertificateId() : "",
                "issuedAt", record != null ? record.getIssuedAt().toString() : ""
        ));
    }

    @GetMapping("/verify/{certificateId}")
    public ResponseEntity<?> verifyCertificate(@PathVariable String certificateId) {
        CertificateRecord record = certificateRepository.findByCertificateId(certificateId).orElse(null);

        if (record == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("valid", false));
        }

        return ResponseEntity.ok(Map.of(
                "valid", true,
                "userName", record.getUserEmail(),
                "completedAt", record.getIssuedAt().toString(),
                "courseTitle", "The Complete Claude AI Course",
                "certificateId", record.getCertificateId()
        ));
    }

    private byte[] generatePdf(String userEmail, String certificateId, java.time.Instant issuedAt) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.setDefaultPageSize(PageSize.A4.rotate());

        Document doc = new Document(pdfDoc);
        doc.setMargins(0, 0, 0, 0);

        float pageWidth = PageSize.A4.rotate().getWidth();
        float pageHeight = PageSize.A4.rotate().getHeight();

        // Background
        PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
        canvas.setFillColor(CREAM);
        canvas.rectangle(0, 0, pageWidth, pageHeight);
        canvas.fill();

        // Top coral border
        canvas.setFillColor(CORAL);
        canvas.rectangle(0, pageHeight - 12, pageWidth, 12);
        canvas.fill();

        // Bottom coral border
        canvas.rectangle(0, 0, pageWidth, 12);
        canvas.fill();

        // Decorative coral line under top border
        canvas.setStrokeColor(CORAL);
        canvas.setLineWidth(0.5f);
        canvas.rectangle(60, 50, pageWidth - 120, pageHeight - 100);
        canvas.stroke();

        doc.setMargins(60, 80, 60, 80);

        // Brand
        doc.add(new Paragraph("LearnClaude.ai")
                .setFontSize(16)
                .setFontColor(MUTED)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(30));

        // Certificate title
        doc.add(new Paragraph("Certificate of Completion")
                .setFontSize(36)
                .setFontColor(CHARCOAL)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(30));

        // Divider line
        doc.add(new Paragraph("________________________")
                .setFontSize(14)
                .setFontColor(CORAL)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(10));

        // "This certifies that"
        doc.add(new Paragraph("This certifies that")
                .setFontSize(14)
                .setFontColor(MUTED)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(25));

        // User email/name
        doc.add(new Paragraph(userEmail)
                .setFontSize(28)
                .setFontColor(CORAL)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(8));

        // "has successfully completed"
        doc.add(new Paragraph("has successfully completed")
                .setFontSize(14)
                .setFontColor(MUTED)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(15));

        // Course title
        doc.add(new Paragraph("The Complete Claude AI Course")
                .setFontSize(22)
                .setFontColor(CHARCOAL)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(8));

        // Subtitle
        doc.add(new Paragraph("17 lessons covering the full Claude ecosystem")
                .setFontSize(12)
                .setFontColor(MUTED)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(6));

        // Date
        String dateStr = DateTimeFormatter.ofPattern("MMMM d, yyyy")
                .withZone(ZoneId.of("America/New_York"))
                .format(issuedAt);
        doc.add(new Paragraph("Completed on " + dateStr)
                .setFontSize(11)
                .setFontColor(MUTED)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(25));

        // Certificate ID + verify link
        doc.add(new Paragraph("Certificate ID: " + certificateId)
                .setFontSize(8)
                .setFontColor(MUTED)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(15));

        doc.add(new Paragraph("Verify at " + siteUrl + "/verify/" + certificateId)
                .setFontSize(8)
                .setFontColor(MUTED)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(2));

        doc.close();
        return out.toByteArray();
    }
}
