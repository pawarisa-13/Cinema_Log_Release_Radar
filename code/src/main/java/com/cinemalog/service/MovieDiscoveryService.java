package com.cinemalog.service;

import java.util.List;
import java.util.Optional;

import com.cinemalog.dto.response.MovieSummaryResponse;

public interface MovieDiscoveryService {

    List<MovieSummaryResponse> nowShowing(int limit);

    List<MovieSummaryResponse> upcoming(int limit);

    List<MovieSummaryResponse> topRated(int limit);

    List<MovieSummaryResponse> recommendedFor(Optional<Long> userId, int limit);

    List<MovieSummaryResponse> similarTo(Long movieId, int limit);
}
