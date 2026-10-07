package com.cinemalog.service.impl;

import java.time.Clock;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import com.cinemalog.domain.entity.Movie;
import com.cinemalog.domain.entity.WatchedMovie;
import com.cinemalog.domain.event.DiaryEntryLoggedEvent;
import com.cinemalog.dto.request.DiaryEntryRequest;
import com.cinemalog.dto.request.UpdateDiaryEntryRequest;
import com.cinemalog.dto.response.DiaryEntryResponse;
import com.cinemalog.dto.response.PageResponse;
import com.cinemalog.exception.BusinessRuleException;
import com.cinemalog.exception.ResourceNotFoundException;
import com.cinemalog.mapper.DiaryMapper;
import com.cinemalog.repository.MovieRepository;
import com.cinemalog.repository.UserRepository;
import com.cinemalog.repository.WatchedMovieRepository;
import com.cinemalog.service.DiaryCommandService;
import com.cinemalog.service.DiaryQueryService;
import com.cinemalog.service.WatchlistService;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class DiaryServiceImpl implements DiaryQueryService, DiaryCommandService {

    private final WatchedMovieRepository watchedMovieRepository;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    private final WatchlistService watchlistService;
    private final ApplicationEventPublisher publisher;
    private final DiaryMapper diaryMapper;
    private final Clock clock;

    public DiaryServiceImpl(WatchedMovieRepository watchedMovieRepository, MovieRepository movieRepository,
                            UserRepository userRepository, WatchlistService watchlistService,
                            ApplicationEventPublisher publisher, DiaryMapper diaryMapper, Clock clock) {
        this.watchedMovieRepository = watchedMovieRepository;
        this.movieRepository = movieRepository;
        this.userRepository = userRepository;
        this.watchlistService = watchlistService;
        this.publisher = publisher;
        this.diaryMapper = diaryMapper;
        this.clock = clock;
    }

    @Override
    public PageResponse<DiaryEntryResponse> list(Long userId, Pageable pageable) {
        Page<WatchedMovie> page = watchedMovieRepository.findByUserId(userId, pageable);
        return PageResponse.of(page, page.getContent().stream().map(diaryMapper::toResponse).toList());
    }

    @Override
    public List<DiaryEntryResponse> month(Long userId, YearMonth month) {
        return between(userId, month.atDay(1), month.atEndOfMonth());
    }

    @Override
    public List<DiaryEntryResponse> day(Long userId, LocalDate date) {
        return between(userId, date, date);
    }

    @Override
    public DiaryEntryResponse get(Long userId, Long entryId) {
        return diaryMapper.toResponse(load(userId, entryId));
    }

    @Override
    @Transactional
    public DiaryEntryResponse create(Long userId, DiaryEntryRequest request) {
        Movie movie = movieRepository.findById(request.movieId())
                .orElseThrow(() -> new ResourceNotFoundException("Movie " + request.movieId() + " was not found."));
        checkDate(movie, request.watchedDate());
        WatchedMovie entry = watchedMovieRepository.save(new WatchedMovie(userRepository.getReferenceById(userId), movie,
                request.watchedDate(), request.rating(), request.review(), request.place()));
        watchlistService.remove(userId, movie.getId());
        publisher.publishEvent(new DiaryEntryLoggedEvent(userId, movie.getId(), movie.getTitle(), entry.hasReview()));
        return diaryMapper.toResponse(entry);
    }

    @Override
    @Transactional
    public DiaryEntryResponse update(Long userId, Long entryId, UpdateDiaryEntryRequest request) {
        WatchedMovie entry = load(userId, entryId);
        checkDate(entry.getMovie(), request.watchedDate());
        boolean hadReview = entry.hasReview();
        entry.update(request.watchedDate(), request.rating(), request.review(), request.place());
        if (!hadReview && entry.hasReview()) {
            publisher.publishEvent(new DiaryEntryLoggedEvent(userId, entry.getMovie().getId(), entry.getMovie().getTitle(), true));
        }
        return diaryMapper.toResponse(entry);
    }

    @Override
    @Transactional
    public void delete(Long userId, Long entryId) {
        watchedMovieRepository.delete(load(userId, entryId));
    }

    private List<DiaryEntryResponse> between(Long userId, LocalDate from, LocalDate to) {
        return watchedMovieRepository.findByUserIdAndWatchedDateBetweenOrderByWatchedDateAscIdAsc(userId, from, to)
                .stream().map(diaryMapper::toResponse).toList();
    }

    private WatchedMovie load(Long userId, Long entryId) {
        return watchedMovieRepository.findByIdAndUserId(entryId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Diary entry " + entryId + " was not found."));
    }

    void checkDate(Movie movie, LocalDate watchedDate) {
        LocalDate today = LocalDate.now(clock);
        if (watchedDate.isAfter(today)) {
            throw new BusinessRuleException("That date is in the future. Pick today or earlier.");
        }
        if (movie.getReleaseDate() != null && movie.getReleaseDate().isAfter(today)) {
            throw new BusinessRuleException(movie.getTitle() + " isn't out until " + movie.getReleaseDate() + ".");
        }
    }
}
