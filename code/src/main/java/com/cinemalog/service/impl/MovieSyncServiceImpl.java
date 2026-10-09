package com.cinemalog.service.impl;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import com.cinemalog.config.TmdbProperties;
import com.cinemalog.domain.entity.Genre;
import com.cinemalog.domain.entity.Movie;
import com.cinemalog.domain.enums.MovieListType;
import com.cinemalog.domain.model.ExternalMovie;
import com.cinemalog.exception.ExternalServiceException;
import com.cinemalog.repository.GenreRepository;
import com.cinemalog.repository.MovieRepository;
import com.cinemalog.service.MovieSyncService;
import com.cinemalog.service.external.MovieCatalogSource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MovieSyncServiceImpl implements MovieSyncService {

    private static final int MAX_SEARCH_IMPORT = 20;

    private final MovieCatalogSource catalogSource;
    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private final TmdbProperties properties;
    private final Clock clock;

    public MovieSyncServiceImpl(MovieCatalogSource catalogSource, MovieRepository movieRepository,
            GenreRepository genreRepository, TmdbProperties properties, Clock clock) {
        this.catalogSource = catalogSource;
        this.movieRepository = movieRepository;
        this.genreRepository = genreRepository;
        this.properties = properties;
        this.clock = clock;
    }

    @Override
    @Transactional(noRollbackFor = ExternalServiceException.class)
    public void syncGenres() {
        if (!catalogSource.isAvailable()) {
            return;
        }
        Map<Integer, String> remote = catalogSource.fetchGenres();
        remote.forEach((id, name) -> {

            if (!genreRepository.existsById(id)) {
                genreRepository.save(new Genre(id, name));
            }
        });
    }

    @Override
    @Transactional(noRollbackFor = ExternalServiceException.class)
    public int syncCatalog() {
        if (!catalogSource.isAvailable()) {
            return 0;
        }
        int pages = properties.syncPages() == null ? 1 : Math.max(1, properties.syncPages());
        int count = 0;
        for (MovieListType type : MovieListType.values()) {
            for (int page = 1; page <= pages; page++) {
                for (ExternalMovie m : catalogSource.fetchList(type, page)) {
                    upsert(m);
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    @Transactional(noRollbackFor = ExternalServiceException.class)
    public int importSearchResults(String query) {
        if (!catalogSource.isAvailable() || query == null || query.isBlank()) {
            return 0;
        }
        List<ExternalMovie> found = catalogSource.search(query.trim()).stream().limit(MAX_SEARCH_IMPORT).toList();
        found.forEach(this::upsert);
        return found.size();
    }

    @Override
    @Transactional(noRollbackFor = ExternalServiceException.class)
    public void refreshDetails(Movie movie) {
        if (!catalogSource.isAvailable()) {
            return;
        }
        catalogSource.fetchDetails(movie.getTmdbId())
                .ifPresent(e -> movie.applyDetails(e, genreRepository.findAllById(e.genreIds()), Instant.now(clock)));
    }

    Movie upsert(ExternalMovie e) {
        Movie movie = movieRepository.findByTmdbId(e.tmdbId()).orElseGet(() -> new Movie(e.tmdbId(), e.title()));
        movie.applySummary(e, genreRepository.findAllById(e.genreIds()));
        return movieRepository.save(movie);
    }
}
