package com.cinemalog.dto.response;

import java.time.LocalDate;
import java.util.List;

import com.cinemalog.domain.enums.NotificationChannel;
import com.cinemalog.domain.enums.ReminderStatus;

public record LibraryStateResponse(List<Long> likedMovieIds, List<Long> watchlistMovieIds,
                                   List<WatchedMarker> watched, List<ReminderMarker> reminders,
                                   long unreadNotifications) {

    public record WatchedMarker(Long movieId, Long entryId, LocalDate watchedDate, Integer rating, boolean reviewed) {
    }

    public record ReminderMarker(Long movieId, int offsetDays, NotificationChannel channel, ReminderStatus status) {
    }
}

