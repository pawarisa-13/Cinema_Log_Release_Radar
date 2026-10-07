package com.cinemalog.controller.api;

import com.cinemalog.common.CurrentUserProvider;
import com.cinemalog.dto.request.FavoriteGenresRequest;
import com.cinemalog.dto.request.UpdateProfileRequest;
import com.cinemalog.dto.response.UserProfileResponse;
import com.cinemalog.service.UserProfileService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users/me")
@Tag(name = "Profile")
public class UserController {

    private final UserProfileService profileService;
    private final CurrentUserProvider currentUser;

    public UserController(UserProfileService profileService, CurrentUserProvider currentUser) {
        this.profileService = profileService;
        this.currentUser = currentUser;
    }

    @GetMapping
    @Operation(summary = "My profile")
    public UserProfileResponse me() {
        return profileService.getProfile(currentUser.requireUserId());
    }

    @PutMapping
    @Operation(summary = "Edit name, email, bio, avatar and favorite genres")
    public UserProfileResponse update(@Valid @RequestBody UpdateProfileRequest request) {
        return profileService.updateProfile(currentUser.requireUserId(), request);
    }

    @PutMapping("/favorite-genres")
    @Operation(summary = "Set favorite genres (sign-up step 2)")
    public UserProfileResponse favoriteGenres(@Valid @RequestBody FavoriteGenresRequest request) {
        return profileService.updateFavoriteGenres(currentUser.requireUserId(), request);
    }
}
