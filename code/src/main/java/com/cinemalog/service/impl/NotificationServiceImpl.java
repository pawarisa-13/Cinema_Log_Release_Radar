package com.cinemalog.service.impl;

import java.util.List;

import com.cinemalog.dto.response.NotificationResponse;
import com.cinemalog.mapper.ReminderMapper;
import com.cinemalog.repository.NotificationRepository;
import com.cinemalog.service.NotificationService;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final ReminderMapper mapper;

    public NotificationServiceImpl(NotificationRepository notificationRepository, ReminderMapper mapper) {
        this.notificationRepository = notificationRepository;
        this.mapper = mapper;
    }

    @Override
    public List<NotificationResponse> latest(Long userId, int limit) {
        int size = Math.max(1, Math.min(limit, 50));
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(0, size))
                .stream().map(mapper::toResponse).toList();
    }

    @Override
    public long unreadCount(Long userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    @Override
    @Transactional
    public void markAllRead(Long userId) {
        notificationRepository.markAllRead(userId);
    }
}
