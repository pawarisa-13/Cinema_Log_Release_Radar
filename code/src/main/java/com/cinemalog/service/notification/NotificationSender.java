package com.cinemalog.service.notification;

import com.cinemalog.domain.enums.NotificationChannel;

public interface NotificationSender {

    NotificationChannel channel();

    void send(NotificationMessage message);
}
