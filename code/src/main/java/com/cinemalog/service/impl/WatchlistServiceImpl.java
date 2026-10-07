package com.cinemalog.service.impl;

import java.util.List;

import com.cinemalog.domain.entity.Movie;
import com.cinemalog.domain.entity.WatchlistItem;
import com.cinemalog.dto.response.MovieSummaryResponse;
import com.cinemalog.exception.ResourceNotFoundException;
import com.cinemalog.mapper.MovieMapper;
import com.cinemalog.repository.MovieRepository;
import com.cinemalog.repository.UserRepository;
import com.cinemalog.repository.WatchlistItemRepository;
import com.cinemalog.service.WatchlistService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class WatchlistServiceImpl implements WatchlistService {

    private final WatchlistItemRepository watchlistRepository;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    private final MovieMapper movieMapper;

    public WatchlistServiceImpl(WatchlistItemRepository watchlistRepository, MovieRepository movieRepository,
                                UserRepository userRepository, MovieMapper movieMapper) {
        this.watchlistRepository = watchlistRepository;
        this.movieRepository = movieRepository;
        this.userRepository = userRepository;
        this.movieMapper = movieMapper;
    }

    @Override
    public List<MovieSummaryResponse> list(Long userId) {
        return watchlistRepository.findByUserIdOrderByAddedAtDesc(userId).stream()
                .map(WatchlistItem::getMovie).map(movieMapper::toSummary).toList();
    }

    @Override
    @Transactional
    public void add(Long userId, Long movieId) {
        if (watchlistRepository.existsByUserIdAndMovieId(userId, movieId)) {
            return;
        }
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie " + movieId + " was not found."));
        watchlistRepository.save(new WatchlistItem(userRepository.getReferenceById(userId), movie));
    }

    @Override
    @Transactional
    public void remove(Long userId, Long movieId) {
        watchlistRepository.findByUserIdAndMovieId(userId, movieId).ifPresent(watchlistRepository::delete);
    }
}
