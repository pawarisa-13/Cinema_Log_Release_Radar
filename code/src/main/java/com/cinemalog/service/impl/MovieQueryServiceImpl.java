package com.cinemalog.service.impl;

import java.time.Clock;
import java.time.Instant;

import com.cinemalog.domain.entity.Movie;
import com.cinemalog.dto.request.MovieSearchCriteria;
import com.cinemalog.dto.response.MovieDetailResponse;
import com.cinemalog.dto.response.MovieSummaryResponse;
import com.cinemalog.dto.response.PageResponse;
import com.cinemalog.exception.ExternalServiceException;
import com.cinemalog.exception.ResourceNotFoundException;
import com.cinemalog.mapper.MovieMapper;
import com.cinemalog.repository.MovieRepository;
import com.cinemalog.repository.specification.MovieSpecificationBuilder;
import com.cinemalog.service.MovieQueryService;
import com.cinemalog.service.MovieSyncService;
import com.cinemalog.service.external.MovieCatalogSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MovieQueryServiceImpl implements MovieQueryService {

    private static final Logger log = LoggerFactory.getLogger(MovieQueryServiceImpl.class);

    private final MovieRepository movieRepository;
    private final MovieSyncService syncService;
    private final MovieCatalogSource catalogSource;
    private final MovieMapper movieMapper;
    private final Clock clock;

    public MovieQueryServiceImpl(MovieRepository movieRepository, MovieSyncService syncService,
            MovieCatalogSource catalogSource, MovieMapper movieMapper, Clock clock) {
        this.movieRepository = movieRepository;
        this.syncService = syncService;
        this.catalogSource = catalogSource;
        this.movieMapper = movieMapper;
        this.clock = clock;
    }

    @Override
    @Transactional
    public PageResponse<MovieSummaryResponse> search(MovieSearchCriteria c) {

        if (c.hasText() && c.page() == 0 && catalogSource.isAvailable()) {
            try {
                syncService.importSearchResults(c.q());
            } catch (ExternalServiceException ex) {
                log.warn("TMDB search failed, showing local results only: {}", ex.getMessage());
            }
        }
        var spec = MovieSpecificationBuilder.create()
                .text(c.q())
                .genre(c.genreId())
                .year(c.year())
                .decade(c.decade())
                .releasedBefore(c.before())
                .minRating(c.minRating())
                .language(c.language())
                .build();
        Page<Movie> page = movieRepository.findAll(spec, PageRequest.of(c.page(), c.size(), c.sort().toSort()));
        return PageResponse.of(page, movieMapper.toSummaries(page.getContent()));
    }

    @Override
    @Transactional
    public MovieDetailResponse getDetail(Long movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie " + movieId + " was not found."));
        if (catalogSource.isAvailable() && movie.needsDetails(Instant.now(clock))) {
            try {
                syncService.refreshDetails(movie);
            } catch (ExternalServiceException ex) {
                log.warn("Could not refresh details for movie {}: {}", movieId, ex.getMessage());
            }
        }
        return movieMapper.toDetail(movie);
    }
}
