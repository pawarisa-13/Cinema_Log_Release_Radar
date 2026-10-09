package com.cinemalog.dto.request;

import java.util.List;

import com.cinemalog.domain.enums.AvatarColor;
import com.cinemalog.domain.enums.AvatarStyle;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @NotBlank(message = "Name is required") @Size(max = 60) String displayName,
        @NotBlank(message = "Email is required") @Email(message = "Email is not valid") String email,
        @Size(max = 200, message = "Bio can be at most 200 characters") String bio,
        @NotNull AvatarStyle avatarStyle,
        @NotNull AvatarColor avatarColor,
        @Size(max = 10, message = "Pick at most 10 genres") List<Integer> favoriteGenreIds) {
}
