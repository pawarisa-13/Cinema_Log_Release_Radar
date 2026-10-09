package com.cinemalog.service.impl;

import java.util.List;

import com.cinemalog.domain.entity.LikedMovie;
import com.cinemalog.domain.entity.Movie;
import com.cinemalog.dto.response.MovieSummaryResponse;
import com.cinemalog.exception.ResourceNotFoundException;
import com.cinemalog.mapper.MovieMapper;
import com.cinemalog.repository.LikedMovieRepository;
import com.cinemalog.repository.MovieRepository;
import com.cinemalog.repository.UserRepository;
import com.cinemalog.service.LikeService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LikeServiceImpl implements LikeService {

    private final LikedMovieRepository likedMovieRepository;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    private final MovieMapper movieMapper;

    public LikeServiceImpl(LikedMovieRepository likedMovieRepository, MovieRepository movieRepository,
                           UserRepository userRepository, MovieMapper movieMapper) {
        this.likedMovieRepository = likedMovieRepository;
        this.movieRepository = movieRepository;
        this.userRepository = userRepository;
        this.movieMapper = movieMapper;
    }

    @Override
    public List<MovieSummaryResponse> list(Long userId) {
        return likedMovieRepository.findByUserIdOrderByLikedAtDesc(userId).stream()
                .map(LikedMovie::getMovie).map(movieMapper::toSummary).toList();
    }

    @Override
    @Transactional
    public void like(Long userId, Long movieId) {
        if (likedMovieRepository.existsByUserIdAndMovieId(userId, movieId)) {
            return;
        }
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie " + movieId + " was not found."));
        likedMovieRepository.save(new LikedMovie(userRepository.getReferenceById(userId), movie));
    }

    @Override
    @Transactional
    public void unlike(Long userId, Long movieId) {
        likedMovieRepository.findByUserIdAndMovieId(userId, movieId).ifPresent(likedMovieRepository::delete);
    }
}
