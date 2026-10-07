package com.cinemalog.service;

import java.util.List;

import com.cinemalog.dto.response.MovieSummaryResponse;

public interface WatchlistService {

    List<MovieSummaryResponse> list(Long userId);

    void add(Long userId, Long movieId);

    void remove(Long userId, Long movieId);
}

