package com.cinemalog.controller.api;

import com.cinemalog.common.CurrentUserProvider;
import com.cinemalog.dto.response.LibraryStateResponse;
import com.cinemalog.dto.response.StatsResponse;
import com.cinemalog.service.LibraryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users/me")
@Tag(name = "Library")
public class LibraryController {

    private final LibraryService libraryService;
    private final CurrentUserProvider currentUser;

    public LibraryController(LibraryService libraryService, CurrentUserProvider currentUser) {
        this.libraryService = libraryService;
        this.currentUser = currentUser;
    }

    @GetMapping("/library")
    @Operation(summary = "Liked / watchlist / watched / reminder markers for drawing movie cards")
    public LibraryStateResponse state() {
        return libraryService.state(currentUser.requireUserId());
    }

    @GetMapping("/stats")
    @Operation(summary = "Profile statistics")
    public StatsResponse stats() {
        return libraryService.stats(currentUser.requireUserId());
    }
}
