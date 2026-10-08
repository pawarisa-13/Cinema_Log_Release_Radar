package com.cinemalog.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProfilePageController {

    @GetMapping("/profile")
    public String profile(Model model) {
        model.addAttribute("nav", "profile");
        return "profile/profile";
    }
}
