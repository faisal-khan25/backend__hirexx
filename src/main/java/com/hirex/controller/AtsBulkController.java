package com.hirex.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.hirex.service.AtsBulkService;
import com.hirex.dto.AtsBulkResponseDto;
import com.hirex.dto.AtsSummaryDto;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/ats")
@CrossOrigin
public class AtsBulkController {
   @Autowired
    private AtsBulkService atsBulkService;

    public AtsBulkController(AtsBulkService atsBulkService) {
        this.atsBulkService = atsBulkService;
    }

    /**
     * POST /api/ats/analyze-all
     *
     * Runs ATS scoring on every uploaded resume in the system.
     * Does NOT update any application status.
     * Safe to call multiple times.
     */
    @PostMapping("/analyze-all")
    public ResponseEntity<AtsBulkResponseDto> analyzeAll() {
        AtsBulkResponseDto response = atsBulkService.analyzeAll();
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/ats/process-all
     *
     * Runs ATS scoring on every uploaded resume AND writes the
     * derived status (HIRED / SHORTLISTED / REJECTED) back to
     * every Application row for that candidate.
     *
     * Status mapping:
     *   score >= 80  →  HIRED
     *   score >= 60  →  SHORTLISTED
     *   score <  60  →  REJECTED
     */
    @PostMapping("/process-all")
    public ResponseEntity<AtsBulkResponseDto> processAll() {
        AtsBulkResponseDto response = atsBulkService.processAll();
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/ats/summary
     *
     * Returns aggregate counts for the manager dashboard:
     *   totalApplicants, totalHired, totalShortlisted,
     *   totalRejected, totalPending, totalWithResume
     */
    @GetMapping("/summary")
    public ResponseEntity<AtsSummaryDto> summary() {
        AtsSummaryDto dto = atsBulkService.getSummary();
        return ResponseEntity.ok(dto);
    }
}
