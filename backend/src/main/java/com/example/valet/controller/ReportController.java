package com.example.valet.controller;

import com.example.valet.dto.ReportAnalyticsResponse;
import com.example.valet.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/analytics")
    public ResponseEntity<ReportAnalyticsResponse> getAnalytics(
            @RequestParam(defaultValue = "daily") String mode,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to
    ) {
        return ResponseEntity.ok(reportService.getAnalytics(mode, from, to));
    }
}
