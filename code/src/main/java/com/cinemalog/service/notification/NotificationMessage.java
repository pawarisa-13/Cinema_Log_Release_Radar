package com.cinemalog.service.notification;

import com.cinemalog.domain.enums.NotificationType;

public record NotificationMessage(Long userId, Long movieId, NotificationType type, String text) {
}
