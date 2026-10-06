package com.cinemalog.mapper;

import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

import com.cinemalog.config.TmdbProperties;
import com.cinemalog.domain.entity.Genre;
import com.cinemalog.domain.entity.Movie;
import com.cinemalog.dto.response.GenreResponse;
import com.cinemalog.dto.response.MovieDetailResponse;
import com.cinemalog.dto.response.MovieSummaryResponse;

import org.springframework.stereotype.Component;

@Component
public class MovieMapper {

    private final TmdbProperties tmdb;
    private final Clock clock;

    public MovieMapper(TmdbProperties tmdb, Clock clock) {
        this.tmdb = tmdb;
        this.clock = clock;
    }

    public MovieSummaryResponse toSummary(Movie m) {
        LocalDate today = LocalDate.now(clock);
        return new MovieSummaryResponse(m.getId(), m.getTmdbId(), m.getTitle(), year(m), m.getReleaseDate(),
                tmdb.imageUrl("w500", m.getPosterPath()), round(m.getVoteAverage()), genreNames(m),
                m.isReleasedBy(today));
    }

    public List<MovieSummaryResponse> toSummaries(List<Movie> movies) {
        return movies.stream().map(this::toSummary).toList();
    }

    public MovieDetailResponse toDetail(Movie m) {
        LocalDate today = LocalDate.now(clock);
        long days = m.getReleaseDate() == null ? 0 : ChronoUnit.DAYS.between(today, m.getReleaseDate());
        String original = (m.getOriginalTitle() != null && !m.getOriginalTitle().equals(m.getTitle()))
                ? m.getOriginalTitle()
                : null;
        return new MovieDetailResponse(m.getId(), m.getTmdbId(), m.getTitle(), original, year(m), m.getReleaseDate(),
                m.getRuntime(), m.getOriginalLanguage(), m.getOverview(),
                tmdb.imageUrl("w500", m.getPosterPath()), tmdb.imageUrl("w1280", m.getBackdropPath()),
                round(m.getVoteAverage()), genreNames(m), m.getDirector(), m.getCastList(), m.isReleasedBy(today),
                days);
    }

    public GenreResponse toGenre(Genre g) {
        return new GenreResponse(g.getId(), g.getName());
    }

    private static Integer year(Movie m) {
        return m.getReleaseDate() == null ? null : m.getReleaseDate().getYear();
    }

    private static Double round(Double v) {
        return v == null ? 0.0 : Math.round(v * 10.0) / 10.0;
    }

    private static List<String> genreNames(Movie m) {
        return m.getGenres().stream().sorted(Comparator.comparing(Genre::getId)).map(Genre::getName).toList();
    }
}
