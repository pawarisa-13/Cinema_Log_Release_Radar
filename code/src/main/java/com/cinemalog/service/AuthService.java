package com.cinemalog.service;

import com.cinemalog.dto.request.RegisterRequest;
import com.cinemalog.dto.response.UserProfileResponse;

public interface AuthService {

    UserProfileResponse register(RegisterRequest request);
}
