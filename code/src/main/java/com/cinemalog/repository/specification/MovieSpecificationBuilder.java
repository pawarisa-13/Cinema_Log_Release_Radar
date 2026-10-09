package com.cinemalog.repository.specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.cinemalog.domain.entity.Genre;
import com.cinemalog.domain.entity.Movie;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

import org.springframework.data.jpa.domain.Specification;

public final class MovieSpecificationBuilder {

    public static final List<String> MAIN_LANGUAGES = List.of("en", "ja", "ko", "th", "fr", "es");

    private final List<Specification<Movie>> parts = new ArrayList<>();

    private MovieSpecificationBuilder() {
    }

    public static MovieSpecificationBuilder create() {
        return new MovieSpecificationBuilder();
    }

    public MovieSpecificationBuilder text(String text) {
        if (text == null || text.isBlank()) {
            return this;
        }
        String like = "%" + text.trim().toLowerCase(Locale.ROOT) + "%";
        parts.add((root, query, cb) -> cb.or(
                cb.like(cb.lower(root.<String>get("title")), like),
                cb.like(cb.lower(root.<String>get("originalTitle")), like),
                cb.like(cb.lower(root.<String>get("director")), like),
                cb.like(cb.lower(root.<String>get("castNames")), like)));
        return this;
    }

    public MovieSpecificationBuilder genre(Integer genreId) {
        if (genreId == null) {
            return this;
        }
        parts.add((root, query, cb) -> {
            Subquery<Long> sub = query.subquery(Long.class);
            Root<Movie> m = sub.from(Movie.class);
            Join<Movie, Genre> g = m.join("genres");
            sub.select(m.<Long>get("id")).where(cb.equal(m.get("id"), root.get("id")), cb.equal(g.get("id"), genreId));
            return cb.exists(sub);
        });
        return this;
    }

    public MovieSpecificationBuilder year(Integer year) {
        if (year == null) {
            return this;
        }
        return releasedBetween(LocalDate.of(year, 1, 1), LocalDate.of(year, 12, 31));
    }

    public MovieSpecificationBuilder decade(Integer firstYear) {
        if (firstYear == null) {
            return this;
        }
        int start = firstYear - Math.floorMod(firstYear, 10);
        return releasedBetween(LocalDate.of(start, 1, 1), LocalDate.of(start + 9, 12, 31));
    }

    public MovieSpecificationBuilder releasedBefore(Integer year) {
        if (year == null) {
            return this;
        }
        LocalDate limit = LocalDate.of(year, 1, 1);
        parts.add((root, query, cb) -> cb.lessThan(root.<LocalDate>get("releaseDate"), limit));
        return this;
    }

    public MovieSpecificationBuilder minRating(Double rating) {
        if (rating == null || rating <= 0) {
            return this;
        }
        parts.add((root, query, cb) -> cb.greaterThanOrEqualTo(root.<Double>get("voteAverage"), rating));
        return this;
    }

    public MovieSpecificationBuilder language(String language) {
        if (language == null || language.isBlank()) {
            return this;
        }
        String lang = language.trim().toLowerCase(Locale.ROOT);
        if ("other".equals(lang)) {
            parts.add((root, query, cb) -> cb.not(root.<String>get("originalLanguage").in(MAIN_LANGUAGES)));
        } else {
            parts.add((root, query, cb) -> cb.equal(root.get("originalLanguage"), lang));
        }
        return this;
    }

    public Specification<Movie> build() {
        return parts.stream().reduce(Specification::and).orElse((root, query, cb) -> cb.conjunction());
    }

    int size() {
        return parts.size();
    }

    private MovieSpecificationBuilder releasedBetween(LocalDate from, LocalDate to) {
        parts.add((root, query, cb) -> cb.between(root.<LocalDate>get("releaseDate"), from, to));
        return this;
    }
}
