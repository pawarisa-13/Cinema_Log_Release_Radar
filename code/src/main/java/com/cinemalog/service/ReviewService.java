package com.cinemalog.service;

import java.util.Optional;

import com.cinemalog.dto.response.ReviewListResponse;

public interface ReviewService {

    ReviewListResponse forMovie(Long movieId, Optional<Long> viewerId);
}
