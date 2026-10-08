package com.cinemalog.mapper;

import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import com.cinemalog.domain.entity.Notification;
import com.cinemalog.domain.entity.Reminder;
import com.cinemalog.dto.response.NotificationResponse;
import com.cinemalog.dto.response.ReminderResponse;

import org.springframework.stereotype.Component;

@Component
public class ReminderMapper {

    private final MovieMapper movieMapper;
    private final Clock clock;

    public ReminderMapper(MovieMapper movieMapper, Clock clock) {
        this.movieMapper = movieMapper;
        this.clock = clock;
    }

    public ReminderResponse toResponse(Reminder r) {
        long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(clock), r.getMovie().getReleaseDate());
        return new ReminderResponse(r.getId(), movieMapper.toSummary(r.getMovie()), r.getOffsetDays(),
                r.getReminderDate(), r.getChannel(), r.getStatus(), daysLeft);
    }

    public NotificationResponse toResponse(Notification n) {
        return new NotificationResponse(n.getId(), n.getType(), n.getChannel(), n.getMessage(), n.isRead(),
                n.getCreatedAt(), n.getMovie() == null ? null : n.getMovie().getId());
    }
}
