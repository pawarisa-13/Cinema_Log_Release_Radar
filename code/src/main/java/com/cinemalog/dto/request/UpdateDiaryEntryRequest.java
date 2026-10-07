package com.cinemalog.dto.request;

import java.time.LocalDate;

import com.cinemalog.domain.enums.WatchPlace;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateDiaryEntryRequest(
        @NotNull(message = "Add the date you watched it") LocalDate watchedDate,
        @Min(value = 1, message = "Ratings go from 1 to 5") @Max(value = 5, message = "Ratings go from 1 to 5") Integer rating,
        @Size(max = 1000) String review,
        @NotNull WatchPlace place) {
}
