package com.cinemalog.controller.api;

import java.util.List;

import com.cinemalog.common.CurrentUserProvider;
import com.cinemalog.dto.request.ReminderRequest;
import com.cinemalog.dto.response.ReminderResponse;
import com.cinemalog.service.ReminderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users/me/reminders")
@Tag(name = "Release reminders")
public class ReminderController {

    private final ReminderService reminderService;
    private final CurrentUserProvider currentUser;

    public ReminderController(ReminderService reminderService, CurrentUserProvider currentUser) {
        this.reminderService = reminderService;
        this.currentUser = currentUser;
    }

    @GetMapping
    @Operation(summary = "Upcoming movies I asked to be reminded about")
    public List<ReminderResponse> list() {
        return reminderService.listActive(currentUser.requireUserId());
    }

    @PutMapping("/{movieId}")
    @Operation(summary = "Create or change the reminder for a movie", description = "400 if the movie is already out")
    public ReminderResponse save(@PathVariable Long movieId, @Valid @RequestBody ReminderRequest request) {
        return reminderService.save(currentUser.requireUserId(), movieId, request);
    }

    @DeleteMapping("/{movieId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove the reminder for a movie")
    public void cancel(@PathVariable Long movieId) {
        reminderService.cancel(currentUser.requireUserId(), movieId);
    }
}
