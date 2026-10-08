package com.cinemalog.dto.response;

import java.util.List;

public record StatsResponse(long moviesWatched, long diaryEntries, long reviews, long watchlist, long collections,
                            long liked, long cinemaVisits, double averageRating, List<GenreCount> topGenres) {

    public record GenreCount(String genre, long count) {
    }
}
