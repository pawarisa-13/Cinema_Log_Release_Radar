package com.cinemalog.controller.api;

import java.util.List;
import java.util.Map;

import com.cinemalog.common.CurrentUserProvider;
import com.cinemalog.dto.response.NotificationResponse;
import com.cinemalog.service.NotificationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users/me/notifications")
@Tag(name = "Notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final CurrentUserProvider currentUser;

    public NotificationController(NotificationService notificationService, CurrentUserProvider currentUser) {
        this.notificationService = notificationService;
        this.currentUser = currentUser;
    }

    @GetMapping
    @Operation(summary = "Latest notifications, newest first")
    public List<NotificationResponse> latest(@RequestParam(defaultValue = "10") @Min(1) @Max(50) int limit) {
        return notificationService.latest(currentUser.requireUserId(), limit);
    }

    @GetMapping("/unread-count")
    public Map<String, Long> unreadCount() {
        return Map.of("unread", notificationService.unreadCount(currentUser.requireUserId()));
    }

    @PostMapping("/read-all")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Mark every notification as read")
    public void readAll() {
        notificationService.markAllRead(currentUser.requireUserId());
    }
}
