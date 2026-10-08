package com.cinemalog.dto.request;

import com.cinemalog.domain.enums.NotificationChannel;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ReminderRequest(
        @NotNull @Min(0) @Max(7) Integer offsetDays,
        @NotNull NotificationChannel channel) {
}
