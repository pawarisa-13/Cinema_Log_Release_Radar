package com.cinemalog.domain.event;

public record DiaryEntryLoggedEvent(Long userId, Long movieId, String movieTitle, boolean reviewAdded) {
}
