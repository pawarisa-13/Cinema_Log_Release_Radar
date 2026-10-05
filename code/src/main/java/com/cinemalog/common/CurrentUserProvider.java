package com.cinemalog.common;

import java.util.Optional;

import com.cinemalog.exception.UnauthorizedException;

public interface CurrentUserProvider {

    Optional<Long> currentUserId();

    default Long requireUserId() {
        return currentUserId().orElseThrow(() -> new UnauthorizedException("Please log in first."));
    }
}
