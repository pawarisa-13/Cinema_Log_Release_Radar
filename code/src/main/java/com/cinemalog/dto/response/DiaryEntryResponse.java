package com.cinemalog.dto.response;

import java.time.Instant;
import java.time.LocalDate;

import com.cinemalog.domain.enums.WatchPlace;

public record DiaryEntryResponse(Long id, MovieSummaryResponse movie, LocalDate watchedDate, Integer rating,
                                 String review, WatchPlace place, Instant createdAt, Instant updatedAt) {
}
