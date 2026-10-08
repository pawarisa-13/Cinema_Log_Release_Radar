package com.cinemalog.controller.api;

import java.util.List;

import com.cinemalog.dto.response.GenreResponse;
import com.cinemalog.service.GenreService;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/genres")
@Tag(name = "Movies")
public class GenreController {

    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping
    public List<GenreResponse> all() {
        return genreService.listAll();
    }
}
