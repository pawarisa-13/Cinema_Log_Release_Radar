package com.cinemalog.service;

import java.util.List;

import com.cinemalog.dto.request.ReminderRequest;
import com.cinemalog.dto.response.ReminderResponse;

public interface ReminderService {

    List<ReminderResponse> listActive(Long userId);

    ReminderResponse save(Long userId, Long movieId, ReminderRequest request);

    void cancel(Long userId, Long movieId);
}
