package com.cinemalog.dto.response;

import java.time.LocalDate;
import java.util.List;

public record MovieDetailResponse(
        Long id,
        Long tmdbId,
        String title,
        String originalTitle,
        Integer year,
        LocalDate releaseDate,
        Integer runtime,
        String language,
        String overview,
        String posterUrl,
        String backdropUrl,
        Double rating,
        List<String> genres,
        String director,
        List<String> cast,
        boolean released,
        long daysUntilRelease) {
}
