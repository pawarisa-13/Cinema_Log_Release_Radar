package com.cinemalog.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class LibraryPageController {

    @GetMapping("/diary")
    public String diary(Model model) {
        model.addAttribute("nav", "diary");
        return "library/diary";
    }

    @GetMapping("/watched")
    public String watched(Model model) {
        return list("watched", model);
    }

    @GetMapping("/watchlist")
    public String watchlist(Model model) {
        return list("watchlist", model);
    }

    @GetMapping("/liked")
    public String liked(Model model) {
        return list("liked", model);
    }

    @GetMapping("/collections")
    public String collections(Model model) {
        model.addAttribute("nav", "profile");
        return "library/collections";
    }

    @GetMapping("/collections/{id}")
    public String collection(@PathVariable Long id, Model model) {
        model.addAttribute("nav", "profile");
        model.addAttribute("collectionId", id);
        return "library/collection";
    }

    private String list(String type, Model model) {
        model.addAttribute("nav", "profile");
        model.addAttribute("listType", type);
        return "library/list";
    }
}
