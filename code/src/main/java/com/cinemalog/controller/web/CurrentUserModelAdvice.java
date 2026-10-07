package com.cinemalog.controller.web;

import com.cinemalog.common.CurrentUserProvider;
import com.cinemalog.service.UserProfileService;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(basePackages = "com.cinemalog.controller.web")
public class CurrentUserModelAdvice {

    private final CurrentUserProvider currentUser;
    private final UserProfileService profileService;

    public CurrentUserModelAdvice(CurrentUserProvider currentUser, UserProfileService profileService) {
        this.currentUser = currentUser;
        this.profileService = profileService;
    }

    @ModelAttribute("me")
    public SessionUserView me() {
        return currentUser.currentUserId().map(profileService::getProfile).map(SessionUserView::new).orElse(null);
    }
}
