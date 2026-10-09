package com.cinemalog.controller.api;

import com.cinemalog.common.CurrentUserProvider;
import com.cinemalog.dto.response.ReviewListResponse;
import com.cinemalog.service.ReviewService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final CurrentUserProvider currentUser;

    public ReviewController(ReviewService reviewService, CurrentUserProvider currentUser) {
        this.reviewService = reviewService;
        this.currentUser = currentUser;
    }

    @GetMapping("/api/v1/movies/{movieId}/reviews")
    @Operation(summary = "\"what people thought\" — reviews and ratings from everyone")
    public ReviewListResponse forMovie(@PathVariable Long movieId) {
        return reviewService.forMovie(movieId, currentUser.currentUserId());
    }
}
