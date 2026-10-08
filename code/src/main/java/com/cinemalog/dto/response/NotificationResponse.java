package com.cinemalog.dto.response;

import java.time.Instant;

import com.cinemalog.domain.enums.NotificationChannel;
import com.cinemalog.domain.enums.NotificationType;

public record NotificationResponse(Long id, NotificationType type, NotificationChannel channel, String message,
                                   boolean read, Instant createdAt, Long movieId) {
}
