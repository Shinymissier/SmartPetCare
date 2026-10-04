package com.petcare.util;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateUtil {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter DISPLAY_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a");

    public static Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        try {
            return Date.valueOf(dateStr.trim());
        } catch (Exception e) {
            try {
                LocalDate ld = LocalDate.parse(dateStr.trim(), DATE_FORMATTER);
                return Date.valueOf(ld);
            } catch (Exception ex) {
                return null;
            }
        }
    }

    public static Timestamp parseTimestamp(String dtStr) {
        if (dtStr == null || dtStr.trim().isEmpty()) {
            return null;
        }
        try {
            // Support HTML5 datetime-local ("2026-10-10T14:30")
            String clean = dtStr.trim().replace("T", " ");
            if (clean.length() == 16) {
                clean += ":00";
            }
            return Timestamp.valueOf(clean);
        } catch (Exception e) {
            return new Timestamp(System.currentTimeMillis());
        }
    }

    public static String formatDate(Date date) {
        if (date == null) return "N/A";
        return date.toLocalDate().format(DISPLAY_FORMATTER);
    }

    public static String formatIsoDate(Date date) {
        if (date == null) return "";
        return date.toLocalDate().format(DATE_FORMATTER);
    }

    public static String formatTimestamp(Timestamp ts) {
        if (ts == null) return "N/A";
        return ts.toLocalDateTime().format(DISPLAY_DATETIME_FORMATTER);
    }

    public static String formatHtmlDatetime(Timestamp ts) {
        if (ts == null) return "";
        return ts.toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
    }

    public static long daysUntil(Date targetDate) {
        if (targetDate == null) return 0;
        return ChronoUnit.DAYS.between(LocalDate.now(), targetDate.toLocalDate());
    }

    public static String calculateVaccinationStatus(Date nextDueDate) {
        if (nextDueDate == null) return "Completed";
        LocalDate now = LocalDate.now();
        LocalDate due = nextDueDate.toLocalDate();
        if (due.isBefore(now)) {
            return "Overdue";
        } else if (!due.isAfter(now.plusDays(30))) {
            return "Upcoming";
        } else {
            return "Completed";
        }
    }
}
