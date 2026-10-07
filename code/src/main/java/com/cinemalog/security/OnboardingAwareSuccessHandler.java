package com.cinemalog.security;

import java.io.IOException;

import com.cinemalog.service.UserProfileService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class OnboardingAwareSuccessHandler implements AuthenticationSuccessHandler {

    private final UserProfileService userProfileService;

    public OnboardingAwareSuccessHandler(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        AppUserPrincipal principal = (AppUserPrincipal) authentication.getPrincipal();
        String target = userProfileService.needsOnboarding(principal.getId()) ? "/onboarding" : "/films";
        response.sendRedirect(request.getContextPath() + target);
    }
}
