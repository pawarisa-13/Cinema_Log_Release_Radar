package com.cinemalog.service.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.cinemalog.domain.entity.WatchedMovie;
import com.cinemalog.domain.enums.WatchPlace;
import com.cinemalog.dto.response.LibraryStateResponse;
import com.cinemalog.dto.response.StatsResponse;
import com.cinemalog.repository.LikedMovieRepository;
import com.cinemalog.repository.MovieCollectionRepository;
import com.cinemalog.repository.WatchedMovieRepository;
import com.cinemalog.repository.WatchlistItemRepository;
import com.cinemalog.service.LibraryService;
import com.cinemalog.service.NotificationService;
import com.cinemalog.service.ReminderService;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LibraryServiceImpl implements LibraryService {

    private static final int TOP_GENRES = 6;

    private final WatchedMovieRepository watchedMovieRepository;
    private final WatchlistItemRepository watchlistRepository;
    private final LikedMovieRepository likedMovieRepository;
    private final MovieCollectionRepository collectionRepository;
    private final ReminderService reminderService;
    private final NotificationService notificationService;

    public LibraryServiceImpl(WatchedMovieRepository watchedMovieRepository, WatchlistItemRepository watchlistRepository,
                              LikedMovieRepository likedMovieRepository, MovieCollectionRepository collectionRepository,
                              ReminderService reminderService, NotificationService notificationService) {
        this.watchedMovieRepository = watchedMovieRepository;
        this.watchlistRepository = watchlistRepository;
        this.likedMovieRepository = likedMovieRepository;
        this.collectionRepository = collectionRepository;
        this.reminderService = reminderService;
        this.notificationService = notificationService;
    }

    @Override
    public LibraryStateResponse state(Long userId) {

        Map<Long, WatchedMovie> latest = new LinkedHashMap<>();
        watchedMovieRepository.findByUserIdOrderByWatchedDateAscIdAsc(userId)
                .forEach(w -> latest.put(w.getMovie().getId(), w));
        List<LibraryStateResponse.WatchedMarker> watched = latest.values().stream()
                .map(w -> new LibraryStateResponse.WatchedMarker(w.getMovie().getId(), w.getId(), w.getWatchedDate(),
                        w.getRating(), w.hasReview()))
                .toList();
        List<LibraryStateResponse.ReminderMarker> reminders = reminderService.listActive(userId).stream()
                .map(r -> new LibraryStateResponse.ReminderMarker(r.movie().id(), r.offsetDays(), r.channel(), r.status()))
                .toList();
        return new LibraryStateResponse(likedMovieRepository.findMovieIds(userId), watchlistRepository.findMovieIds(userId),
                watched, reminders, notificationService.unreadCount(userId));
    }

    @Override
    public StatsResponse stats(Long userId) {
        List<StatsResponse.GenreCount> genres = watchedMovieRepository.countGenres(userId, PageRequest.of(0, TOP_GENRES))
                .stream().map(g -> new StatsResponse.GenreCount(g.getName(), g.getTotal())).toList();
        Double avg = watchedMovieRepository.averageRating(userId);
        return new StatsResponse(
                watchedMovieRepository.countDistinctMovies(userId),
                watchedMovieRepository.countByUserId(userId),
                watchedMovieRepository.countByUserIdAndReviewIsNotNull(userId),
                watchlistRepository.countByUserId(userId),
                collectionRepository.countByUserId(userId),
                likedMovieRepository.countByUserId(userId),
                watchedMovieRepository.countByUserIdAndPlace(userId, WatchPlace.CINEMA),
                avg == null ? 0 : Math.round(avg * 10) / 10.0,
                genres);
    }
}
