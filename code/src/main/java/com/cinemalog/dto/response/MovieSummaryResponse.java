package com.cinemalog.dto.response;

import java.time.LocalDate;
import java.util.List;

public record MovieSummaryResponse(
        Long id,
        Long tmdbId,
        String title,
        Integer year,
        LocalDate releaseDate,
        String posterUrl,
        Double rating,
        List<String> genres,
        boolean released) {
}
