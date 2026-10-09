package com.cinemalog.controller.api;

import java.util.List;

import com.cinemalog.common.CurrentUserProvider;
import com.cinemalog.dto.response.MovieSummaryResponse;
import com.cinemalog.service.WatchlistService;

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
@RequestMapping("/api/v1/users/me/watchlist")
@Tag(name = "Watchlist")
public class WatchlistController {

    private final WatchlistService watchlistService;
    private final CurrentUserProvider currentUser;

    public WatchlistController(WatchlistService watchlistService, CurrentUserProvider currentUser) {
        this.watchlistService = watchlistService;
        this.currentUser = currentUser;
    }

    @GetMapping
    @Operation(summary = "Things I want to watch")
    public List<MovieSummaryResponse> list() {
        return watchlistService.list(currentUser.requireUserId());
    }

    @PutMapping("/{movieId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void add(@PathVariable Long movieId) {
        watchlistService.add(currentUser.requireUserId(), movieId);
    }

    @DeleteMapping("/{movieId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable Long movieId) {
        watchlistService.remove(currentUser.requireUserId(), movieId);
    }
}
