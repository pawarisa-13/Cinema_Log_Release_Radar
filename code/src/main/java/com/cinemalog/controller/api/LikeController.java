package com.cinemalog.controller.api;

import java.util.List;

import com.cinemalog.common.CurrentUserProvider;
import com.cinemalog.dto.response.MovieSummaryResponse;
import com.cinemalog.service.LikeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users/me/likes")
@Tag(name = "Likes")
public class LikeController {

    private final LikeService likeService;
    private final CurrentUserProvider currentUser;

    public LikeController(LikeService likeService, CurrentUserProvider currentUser) {
        this.likeService = likeService;
        this.currentUser = currentUser;
    }

    @GetMapping
    @Operation(summary = "Movies I liked (independent from the watchlist)")
    public List<MovieSummaryResponse> list() {
        return likeService.list(currentUser.requireUserId());
    }

    @PutMapping("/{movieId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void add(@PathVariable Long movieId) {
        likeService.like(currentUser.requireUserId(), movieId);
    }

    @DeleteMapping("/{movieId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable Long movieId) {
        likeService.unlike(currentUser.requireUserId(), movieId);
    }
}
