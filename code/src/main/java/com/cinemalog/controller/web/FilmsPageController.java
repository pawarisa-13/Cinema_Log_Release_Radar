package com.cinemalog.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class FilmsPageController {

    @GetMapping("/")
    public String home() {
        return "redirect:/films";
    }

    @GetMapping("/films")
    public String films(Model model) {
        model.addAttribute("nav", "films");
        return "films/films";
    }

    @GetMapping("/catalog")
    public String catalog(Model model) {
        model.addAttribute("nav", "films");
        return "films/catalog";
    }

    @GetMapping("/movies/{id}")
    public String movie(@PathVariable Long id, Model model) {
        model.addAttribute("nav", "films");
        model.addAttribute("movieId", id);
        return "films/movie";
    }
}
