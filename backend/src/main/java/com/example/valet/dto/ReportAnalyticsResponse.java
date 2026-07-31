package com.example.valet.dto;

import java.util.List;

public record ReportAnalyticsResponse(
        String mode, // "daily" or "monthly"
        String fromDate,
        String toDate,
        long totalDelivered,
        long totalParked,
        long totalRequested,
        List<DataPoint> dataPoints
) {
    public record DataPoint(String label, long count) {}
}
