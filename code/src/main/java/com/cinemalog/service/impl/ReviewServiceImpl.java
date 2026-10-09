package com.cinemalog.service.impl;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.cinemalog.dto.response.ReviewListResponse;
import com.cinemalog.dto.response.ReviewResponse;
import com.cinemalog.exception.ResourceNotFoundException;
import com.cinemalog.mapper.DiaryMapper;
import com.cinemalog.repository.MovieRepository;
import com.cinemalog.repository.WatchedMovieRepository;
import com.cinemalog.service.ReviewService;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ReviewServiceImpl implements ReviewService {

    private static final int MAX_REVIEWS = 30;

    private final WatchedMovieRepository watchedMovieRepository;
    private final MovieRepository movieRepository;
    private final DiaryMapper diaryMapper;

    public ReviewServiceImpl(WatchedMovieRepository watchedMovieRepository, MovieRepository movieRepository,
                             DiaryMapper diaryMapper) {
        this.watchedMovieRepository = watchedMovieRepository;
        this.movieRepository = movieRepository;
        this.diaryMapper = diaryMapper;
    }

    @Override
    public ReviewListResponse forMovie(Long movieId, Optional<Long> viewerId) {
        if (!movieRepository.existsById(movieId)) {
            throw new ResourceNotFoundException("Movie " + movieId + " was not found.");
        }
        Long viewer = viewerId.orElse(null);
        List<ReviewResponse> reviews = watchedMovieRepository.findReviewsForMovie(movieId, PageRequest.of(0, MAX_REVIEWS))
                .stream()
                .map(w -> diaryMapper.toReview(w, viewer))
                .sorted(Comparator.comparing(ReviewResponse::mine).reversed())
                .toList();
        List<Integer> ratings = reviews.stream().map(ReviewResponse::rating).filter(Objects::nonNull).toList();
        double average = ratings.stream().mapToInt(Integer::intValue).average().orElse(0);
        return new ReviewListResponse(Math.round(average * 10) / 10.0, ratings.size(), reviews);
    }
}
