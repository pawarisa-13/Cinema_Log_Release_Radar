package com.cinemalog.domain.event;

import com.cinemalog.domain.enums.NotificationChannel;

public record ReminderDueEvent(Long reminderId, Long userId, Long movieId, String message, NotificationChannel channel) {
}
