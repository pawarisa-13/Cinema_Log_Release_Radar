package com.cinemalog.dto.response;

import java.time.LocalDate;

import com.cinemalog.domain.enums.AvatarColor;
import com.cinemalog.domain.enums.AvatarStyle;

public record ReviewResponse(Long id, String displayName, AvatarStyle avatarStyle, AvatarColor avatarColor,
                             Integer rating, String review, LocalDate watchedDate, boolean mine) {
}
