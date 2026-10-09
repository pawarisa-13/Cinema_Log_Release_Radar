package com.cinemalog.domain.entity;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.cinemalog.domain.model.ExternalMovie;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import org.hibernate.annotations.BatchSize;

@Entity
@Table(name = "movies")
public class Movie {

    private static final Duration DETAILS_TTL = Duration.ofDays(7);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tmdb_id", nullable = false, unique = true)
    private Long tmdbId;

    @Column(nullable = false, length = 300)
    private String title;

    @Column(name = "original_title", length = 300)
    private String originalTitle;

    @Column(columnDefinition = "text")
    private String overview;

    @Column(name = "poster_path", length = 200)
    private String posterPath;

    @Column(name = "backdrop_path", length = 200)
    private String backdropPath;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    private Integer runtime;

    @Column(name = "vote_average", nullable = false)
    private Double voteAverage = 0.0;

    @Column(nullable = false)
    private Double popularity = 0.0;

    @Column(name = "original_language", length = 10)
    private String originalLanguage;

    @Column(length = 200)
    private String director;

    @Column(name = "cast_names", length = 500)
    private String castNames;

    @Column(name = "details_fetched_at")
    private Instant detailsFetchedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "movie_genres", joinColumns = @JoinColumn(name = "movie_id"), inverseJoinColumns = @JoinColumn(name = "genre_id"))
    @BatchSize(size = 50)
    private Set<Genre> genres = new LinkedHashSet<>();

    protected Movie() {
    }

    public Movie(Long tmdbId, String title) {
        this.tmdbId = tmdbId;
        this.title = title;
        this.createdAt = Instant.now();
    }

    public void applySummary(ExternalMovie source, Collection<Genre> resolvedGenres) {
        this.title = source.title();
        this.originalTitle = source.originalTitle();
        this.overview = source.overview();
        this.posterPath = source.posterPath();
        this.backdropPath = source.backdropPath();
        this.releaseDate = source.releaseDate();
        this.voteAverage = source.voteAverage() == null ? 0.0 : source.voteAverage();
        this.popularity = source.popularity() == null ? 0.0 : source.popularity();
        this.originalLanguage = source.originalLanguage();
        if (!resolvedGenres.isEmpty()) {
            this.genres.clear();
            this.genres.addAll(resolvedGenres);
        }
    }

    public void applyDetails(ExternalMovie source, Collection<Genre> resolvedGenres, Instant fetchedAt) {
        applySummary(source, resolvedGenres);
        this.runtime = source.runtime();
        this.director = source.director();
        this.castNames = source.cast() == null ? null : String.join(", ", source.cast());
        if (this.castNames != null && this.castNames.length() > 500) {
            this.castNames = this.castNames.substring(0, 500);
        }
        this.detailsFetchedAt = fetchedAt;
    }

    public boolean needsDetails(Instant now) {
        return detailsFetchedAt == null || detailsFetchedAt.plus(DETAILS_TTL).isBefore(now);
    }

    public boolean isReleasedBy(LocalDate day) {
        return releaseDate != null && !releaseDate.isAfter(day);
    }

    public List<String> getCastList() {
        if (castNames == null || castNames.isBlank()) {
            return List.of();
        }
        return Arrays.stream(castNames.split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList();
    }

    public Long getId() {
        return id;
    }

    public Long getTmdbId() {
        return tmdbId;
    }

    public String getTitle() {
        return title;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getOverview() {
        return overview;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public Integer getRuntime() {
        return runtime;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public Double getPopularity() {
        return popularity;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public String getDirector() {
        return director;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Set<Genre> getGenres() {
        return genres;
    }
}
