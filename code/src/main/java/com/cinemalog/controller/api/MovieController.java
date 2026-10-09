package com.cinemalog.controller.api;

import java.util.List;

import com.cinemalog.common.CurrentUserProvider;
import com.cinemalog.dto.request.MovieSearchCriteria;
import com.cinemalog.dto.response.MovieDetailResponse;
import com.cinemalog.dto.response.MovieSummaryResponse;
import com.cinemalog.dto.response.PageResponse;
import com.cinemalog.service.MovieDiscoveryService;
import com.cinemalog.service.MovieQueryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/movies")
@Tag(name = "Movies", description = "Catalog, search and discovery shelves")
public class MovieController {

    private final MovieQueryService queryService;
    private final MovieDiscoveryService discoveryService;
    private final CurrentUserProvider currentUser;

    public MovieController(MovieQueryService queryService, MovieDiscoveryService discoveryService,
            CurrentUserProvider currentUser) {
        this.queryService = queryService;
        this.discoveryService = discoveryService;
        this.currentUser = currentUser;
    }

    @GetMapping
    @Operation(summary = "Search & filter with pagination and sorting", description = "e.g. /api/v1/movies?q=ghost&genreId=27&decade=2010&minRating=7&sort=RATING&page=0&size=40")
    public PageResponse<MovieSummaryResponse> search(@Valid @ParameterObject MovieSearchCriteria criteria) {
        return queryService.search(criteria);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Movie details (refreshed from TMDB when older than a week)")
    public MovieDetailResponse detail(@PathVariable Long id) {
        return queryService.getDetail(id);
    }

    @GetMapping("/{id}/similar")
    @Operation(summary = "\"you might also like...\"")
    public List<MovieSummaryResponse> similar(@PathVariable Long id,
            @RequestParam(defaultValue = "12") @Min(1) @Max(30) int limit) {
        return discoveryService.similarTo(id, limit);
    }

    @GetMapping("/now-showing")
    public List<MovieSummaryResponse> nowShowing(@RequestParam(defaultValue = "16") @Min(1) @Max(40) int limit) {
        return discoveryService.nowShowing(limit);
    }

    @GetMapping("/upcoming")
    public List<MovieSummaryResponse> upcoming(@RequestParam(defaultValue = "12") @Min(1) @Max(40) int limit) {
        return discoveryService.upcoming(limit);
    }

    @GetMapping("/top-rated")
    public List<MovieSummaryResponse> topRated(@RequestParam(defaultValue = "16") @Min(1) @Max(40) int limit) {
        return discoveryService.topRated(limit);
    }

    @GetMapping("/recommended")
    @Operation(summary = "\"picked for you\" — based on my favorite genres (top rated when logged out)")
    public List<MovieSummaryResponse> recommended(@RequestParam(defaultValue = "14") @Min(1) @Max(40) int limit) {
        return discoveryService.recommendedFor(currentUser.currentUserId(), limit);
    }
}
