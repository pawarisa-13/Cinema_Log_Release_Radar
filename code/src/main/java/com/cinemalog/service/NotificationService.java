package com.cinemalog.service;

import java.util.List;

import com.cinemalog.dto.response.NotificationResponse;

public interface NotificationService {

    List<NotificationResponse> latest(Long userId, int limit);

    long unreadCount(Long userId);

    void markAllRead(Long userId);
}
