package com.cinemalog.service;

import java.util.List;

import com.cinemalog.dto.response.MovieSummaryResponse;

public interface LikeService {

    List<MovieSummaryResponse> list(Long userId);

    void like(Long userId, Long movieId);

    void unlike(Long userId, Long movieId);
}
