package com.cognizant.uams.controller;

import com.cognizant.uams.entity.AcademicRecord;
import com.cognizant.uams.service.TranscriptService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transcripts")
public class TranscriptController {
    private final TranscriptService transcriptService;

    public TranscriptController(TranscriptService transcriptService) {
        this.transcriptService = transcriptService;
    }

    @GetMapping("/{studentId}/raw")
    public List<AcademicRecord> getAcademicRecord(@PathVariable Integer studentId) {
        return transcriptService.getAcademicRecord(studentId);
    }

    @GetMapping("/{studentId}/summary")
    public Map<String, Object> generateTranscript(@PathVariable Integer studentId) {
        return transcriptService.generateTranscript(studentId);
    }

    @GetMapping(value = "/{studentId}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> downloadTranscript(@PathVariable Integer studentId) {
        ContentDisposition disposition = ContentDisposition.attachment()
                .filename("student-" + studentId + "-grade-sheet.pdf", StandardCharsets.UTF_8)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .body(transcriptService.generatePdf(studentId));
    }
}
