package com.cinemalog.domain.model;

import java.time.LocalDate;
import java.util.List;

public record ExternalMovie(
        Long tmdbId,
        String title,
        String originalTitle,
        String overview,
        String posterPath,
        String backdropPath,
        LocalDate releaseDate,
        Double voteAverage,
        Double popularity,
        String originalLanguage,
        List<Integer> genreIds,
        Integer runtime,
        String director,
        List<String> cast) {
}
