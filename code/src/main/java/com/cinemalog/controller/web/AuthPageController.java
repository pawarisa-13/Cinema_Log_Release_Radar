package com.cinemalog.controller.web;

import com.cinemalog.common.CurrentUserProvider;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthPageController {

    private final CurrentUserProvider currentUser;

    public AuthPageController(CurrentUserProvider currentUser) {
        this.currentUser = currentUser;
    }

    @GetMapping("/login")
    public String login() {
        return currentUser.currentUserId().isPresent() ? "redirect:/films" : "auth/login";
    }

    @GetMapping("/register")
    public String register() {
        return currentUser.currentUserId().isPresent() ? "redirect:/films" : "auth/register";
    }

    @GetMapping("/onboarding")
    public String onboarding() {
        return "auth/onboarding";
    }
}
