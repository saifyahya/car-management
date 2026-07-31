package com.example.valet.service;

import com.example.valet.dto.ReportAnalyticsResponse;
import com.example.valet.entity.TicketStatus;
import com.example.valet.entity.ValetTicket;
import com.example.valet.repository.ValetTicketRepository;
import com.example.valet.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Service
public class ReportService {
    private final ValetTicketRepository ticketRepository;

    public ReportService(ValetTicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Transactional(readOnly = true)
    public ReportAnalyticsResponse getAnalytics(String mode, String fromStr, String toStr) {
        Long clientId = SecurityUtils.getCurrentClientId();
        LocalDate now = LocalDate.now();
        boolean isMonthly = "monthly".equalsIgnoreCase(mode);

        LocalDate defaultFrom;
        LocalDate defaultTo;

        if (isMonthly) {
            defaultFrom = now.withDayOfYear(1);
            defaultTo = now.withDayOfMonth(now.lengthOfMonth());
        } else {
            defaultFrom = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            defaultTo = now;
        }

        LocalDate fromDate = parseDateStr(fromStr, isMonthly, false);
        if (fromDate == null) fromDate = defaultFrom;

        LocalDate toDate = parseDateStr(toStr, isMonthly, true);
        if (toDate == null) toDate = defaultTo;

        if (fromDate.isAfter(toDate)) {
            LocalDate tmp = fromDate;
            fromDate = toDate;
            toDate = tmp;
        }

        List<ValetTicket> allTickets = ticketRepository.findAllByClientIdOrderByCheckedInAtDesc(clientId);

        LocalDate finalFromDate = fromDate;
        LocalDate finalToDate = toDate;
        ZoneId zone = ZoneId.systemDefault();

        List<ValetTicket> deliveredTickets = allTickets.stream()
                .filter(t -> t.getStatus() == TicketStatus.DELIVERED)
                .filter(t -> {
                    if (t.getDeliveredAt() == null) return false;
                    LocalDate dDate = t.getDeliveredAt().atZone(zone).toLocalDate();
                    return !dDate.isBefore(finalFromDate) && !dDate.isAfter(finalToDate);
                })
                .toList();

        long totalDelivered = deliveredTickets.size();
        long totalParked = allTickets.stream().filter(t -> t.getStatus() == TicketStatus.PARKED).count();
        long totalRequested = allTickets.stream().filter(t -> t.getStatus() == TicketStatus.REQUESTED).count();

        DateTimeFormatter formatter = isMonthly ? DateTimeFormatter.ofPattern("yyyy-MM") : DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Map<String, Long> countsByLabel = new LinkedHashMap<>();

        if (isMonthly) {
            LocalDate cursor = finalFromDate.withDayOfMonth(1);
            LocalDate endMonth = finalToDate.withDayOfMonth(1);
            while (!cursor.isAfter(endMonth)) {
                countsByLabel.put(cursor.format(formatter), 0L);
                cursor = cursor.plusMonths(1);
            }
        } else {
            LocalDate cursor = finalFromDate;
            while (!cursor.isAfter(finalToDate)) {
                countsByLabel.put(cursor.format(formatter), 0L);
                cursor = cursor.plusDays(1);
            }
        }

        for (ValetTicket t : deliveredTickets) {
            if (t.getDeliveredAt() != null) {
                String label = t.getDeliveredAt().atZone(zone).toLocalDate().format(formatter);
                countsByLabel.put(label, countsByLabel.getOrDefault(label, 0L) + 1);
            }
        }

        List<ReportAnalyticsResponse.DataPoint> dataPoints = countsByLabel.entrySet().stream()
                .map(e -> new ReportAnalyticsResponse.DataPoint(e.getKey(), e.getValue()))
                .toList();

        return new ReportAnalyticsResponse(
                isMonthly ? "monthly" : "daily",
                isMonthly ? YearMonth.from(fromDate).toString() : fromDate.toString(),
                isMonthly ? YearMonth.from(toDate).toString() : toDate.toString(),
                totalDelivered,
                totalParked,
                totalRequested,
                dataPoints
        );
    }

    private LocalDate parseDateStr(String str, boolean isMonthly, boolean isEnd) {
        if (str == null || str.isBlank()) return null;
        try {
            if (str.length() == 7) {
                YearMonth ym = YearMonth.parse(str);
                return isEnd ? ym.atEndOfMonth() : ym.atDay(1);
            }
            return LocalDate.parse(str);
        } catch (Exception e) {
            return null;
        }
    }
}
