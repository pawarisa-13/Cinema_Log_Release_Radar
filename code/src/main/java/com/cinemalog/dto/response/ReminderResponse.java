package com.cinemalog.dto.response;

import java.time.LocalDate;

import com.cinemalog.domain.enums.NotificationChannel;
import com.cinemalog.domain.enums.ReminderStatus;

public record ReminderResponse(Long id, MovieSummaryResponse movie, int offsetDays, LocalDate reminderDate,
                               NotificationChannel channel, ReminderStatus status, long daysLeft) {
}
