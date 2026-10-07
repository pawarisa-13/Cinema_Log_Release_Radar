package com.cinemalog.dto.response;

import java.util.List;

import com.cinemalog.domain.enums.AvatarColor;
import com.cinemalog.domain.enums.AvatarStyle;

public record UserProfileResponse(Long id, String email, String displayName, String bio,
                                  AvatarStyle avatarStyle, AvatarColor avatarColor,
                                  List<GenreResponse> favoriteGenres) {
}
