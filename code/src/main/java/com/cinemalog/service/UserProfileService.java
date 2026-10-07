package com.cinemalog.service;

import com.cinemalog.dto.request.FavoriteGenresRequest;
import com.cinemalog.dto.request.UpdateProfileRequest;
import com.cinemalog.dto.response.UserProfileResponse;

public interface UserProfileService {

    UserProfileResponse getProfile(Long userId);

    UserProfileResponse updateProfile(Long userId, UpdateProfileRequest request);

    UserProfileResponse updateFavoriteGenres(Long userId, FavoriteGenresRequest request);

    boolean needsOnboarding(Long userId);
}
