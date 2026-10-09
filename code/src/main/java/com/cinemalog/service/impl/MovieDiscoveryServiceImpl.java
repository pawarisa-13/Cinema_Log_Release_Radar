package com.cinemalog.service.impl;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.cinemalog.domain.entity.Genre;
import com.cinemalog.domain.entity.Movie;
import com.cinemalog.dto.response.MovieSummaryResponse;
import com.cinemalog.exception.ResourceNotFoundException;
import com.cinemalog.mapper.MovieMapper;
import com.cinemalog.repository.MovieRepository;
import com.cinemalog.repository.UserRepository;
import com.cinemalog.repository.WatchedMovieRepository;
import com.cinemalog.service.MovieDiscoveryService;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MovieDiscoveryServiceImpl implements MovieDiscoveryService {

    static final int NOW_SHOWING_WINDOW_DAYS = 60;
    static final double TOP_RATED_MIN = 7.5;

    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    private final WatchedMovieRepository watchedMovieRepository;
    private final MovieMapper movieMapper;
    private final Clock clock;

    public MovieDiscoveryServiceImpl(MovieRepository movieRepository, UserRepository userRepository,
            WatchedMovieRepository watchedMovieRepository, MovieMapper movieMapper, Clock clock) {
        this.movieRepository = movieRepository;
        this.userRepository = userRepository;
        this.watchedMovieRepository = watchedMovieRepository;
        this.movieMapper = movieMapper;
        this.clock = clock;
    }

    @Override
    public List<MovieSummaryResponse> nowShowing(int limit) {
        LocalDate today = today();
        return movieMapper.toSummaries(movieRepository.findByReleaseDateBetweenOrderByPopularityDesc(
                today.minusDays(NOW_SHOWING_WINDOW_DAYS), today, PageRequest.of(0, limit)));
    }

    @Override
    public List<MovieSummaryResponse> upcoming(int limit) {
        return movieMapper.toSummaries(movieRepository.findByReleaseDateAfterOrderByReleaseDateAscPopularityDesc(
                today(), PageRequest.of(0, limit)));
    }

    @Override
    public List<MovieSummaryResponse> topRated(int limit) {
        return movieMapper.toSummaries(topRatedMovies(limit));
    }

    @Override
    public List<MovieSummaryResponse> recommendedFor(Optional<Long> userId, int limit) {
        if (userId.isEmpty()) {
            return topRated(limit);
        }
        Set<Integer> favorites = userRepository.findById(userId.get())
                .map(u -> u.getProfile().getFavoriteGenres().stream().map(Genre::getId).collect(Collectors.toSet()))
                .orElse(Set.of());
        if (favorites.isEmpty()) {
            return topRated(limit);
        }
        Set<Long> watched = watchedMovieRepository.findWatchedMovieIds(userId.get());
        List<Movie> picks = movieRepository.findReleasedWithAnyGenre(favorites, today(), PageRequest.of(0, limit * 4))
                .stream()
                .filter(m -> !watched.contains(m.getId()))
                .sorted(Comparator.comparingDouble((Movie m) -> score(m, favorites, null)).reversed())
                .limit(limit)
                .toList();
        return movieMapper.toSummaries(picks);
    }

    @Override
    public List<MovieSummaryResponse> similarTo(Long movieId, int limit) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie " + movieId + " was not found."));
        Set<Integer> genres = movie.getGenres().stream().map(Genre::getId).collect(Collectors.toSet());
        if (genres.isEmpty()) {
            return topRated(limit);
        }
        List<Movie> picks = movieRepository.findReleasedWithAnyGenre(genres, today(), PageRequest.of(0, 60))
                .stream()
                .filter(m -> !m.getId().equals(movieId))
                .sorted(Comparator.comparingDouble((Movie m) -> score(m, genres, movie.getDirector())).reversed())
                .limit(limit)
                .toList();
        return movieMapper.toSummaries(picks);
    }

    static double score(Movie candidate, Set<Integer> genreIds, String director) {
        long shared = candidate.getGenres().stream().filter(g -> genreIds.contains(g.getId())).count();
        double bonus = director != null && Objects.equals(director, candidate.getDirector()) ? 2 : 0;
        return shared * 3 + candidate.getVoteAverage() / 10.0 + bonus;
    }

    private List<Movie> topRatedMovies(int limit) {
        return movieRepository
                .findByReleaseDateLessThanEqualAndVoteAverageGreaterThanEqualOrderByVoteAverageDescPopularityDesc(
                        today(), TOP_RATED_MIN, PageRequest.of(0, limit));
    }

    private LocalDate today() {
        return LocalDate.now(clock);
    }
}
