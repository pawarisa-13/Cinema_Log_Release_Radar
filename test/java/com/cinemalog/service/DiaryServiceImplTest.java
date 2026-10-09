package com.cinemalog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import com.cinemalog.domain.entity.Movie;
import com.cinemalog.domain.entity.User;
import com.cinemalog.domain.entity.WatchedMovie;
import com.cinemalog.domain.enums.WatchPlace;
import com.cinemalog.domain.event.DiaryEntryLoggedEvent;
import com.cinemalog.domain.model.ExternalMovie;
import com.cinemalog.dto.request.DiaryEntryRequest;
import com.cinemalog.dto.request.UpdateDiaryEntryRequest;
import com.cinemalog.exception.BusinessRuleException;
import com.cinemalog.exception.ResourceNotFoundException;
import com.cinemalog.mapper.DiaryMapper;
import com.cinemalog.repository.MovieRepository;
import com.cinemalog.repository.UserRepository;
import com.cinemalog.repository.WatchedMovieRepository;
import com.cinemalog.service.impl.DiaryServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

class DiaryServiceImplTest {

    static final LocalDate TODAY = LocalDate.of(2026, 10, 3);

    WatchedMovieRepository repo = mock(WatchedMovieRepository.class);
    MovieRepository movies = mock(MovieRepository.class);
    UserRepository users = mock(UserRepository.class);
    WatchlistService watchlist = mock(WatchlistService.class);
    ApplicationEventPublisher publisher = mock(ApplicationEventPublisher.class);
    DiaryMapper mapper = mock(DiaryMapper.class);
    DiaryServiceImpl service;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(TODAY.atStartOfDay(ZoneOffset.UTC).toInstant(), ZoneId.of("UTC"));
        service = new DiaryServiceImpl(repo, movies, users, watchlist, publisher, mapper, clock);
    }

    static Movie movie(long id, LocalDate release) {
        Movie m = new Movie(id, "Movie " + id);
        m.applySummary(new ExternalMovie(id, "Movie " + id, null, null, null, null, release, 7.0, 1.0, "en",
                List.of(), null, null, null), List.of());
        ReflectionTestUtils.setField(m, "id", id);
        return m;
    }

    @Test
    void cannotLogAFutureDate() {
        when(movies.findById(1L)).thenReturn(Optional.of(movie(1L, TODAY.minusYears(1))));

        assertThatThrownBy(() -> service.create(7L, new DiaryEntryRequest(1L, TODAY.plusDays(1), 4, null, WatchPlace.HOME)))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("future");
    }

    @Test
    void cannotLogAMovieThatIsNotOutYet() {
        when(movies.findById(1L)).thenReturn(Optional.of(movie(1L, TODAY.plusDays(10))));

        assertThatThrownBy(() -> service.create(7L, new DiaryEntryRequest(1L, TODAY, 4, null, WatchPlace.CINEMA)))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void loggingSavesRemovesFromWatchlistAndAnnouncesTheReview() {
        when(movies.findById(1L)).thenReturn(Optional.of(movie(1L, TODAY.minusDays(5))));
        when(users.getReferenceById(7L)).thenReturn(mock(User.class));
        when(repo.save(any(WatchedMovie.class))).thenAnswer(inv -> inv.getArgument(0));

        service.create(7L, new DiaryEntryRequest(1L, TODAY, 5, "  loved it  ", WatchPlace.CINEMA));

        ArgumentCaptor<WatchedMovie> saved = ArgumentCaptor.forClass(WatchedMovie.class);
        verify(repo).save(saved.capture());
        assertThat(saved.getValue().getReview()).isEqualTo("loved it");
        verify(watchlist).remove(7L, 1L);
        ArgumentCaptor<DiaryEntryLoggedEvent> event = ArgumentCaptor.forClass(DiaryEntryLoggedEvent.class);
        verify(publisher).publishEvent(event.capture());
        assertThat(event.getValue().reviewAdded()).isTrue();
    }

    @Test
    void editingWithoutAddingAReviewDoesNotNotify() {
        WatchedMovie entry = new WatchedMovie(mock(User.class), movie(1L, TODAY.minusDays(5)), TODAY, 3, null, WatchPlace.HOME);
        when(repo.findByIdAndUserId(9L, 7L)).thenReturn(Optional.of(entry));

        service.update(7L, 9L, new UpdateDiaryEntryRequest(TODAY.minusDays(1), 4, "", WatchPlace.HOME));

        assertThat(entry.getRating()).isEqualTo(4);
        assertThat(entry.getWatchedDate()).isEqualTo(TODAY.minusDays(1));
        verify(publisher, never()).publishEvent(any(Object.class));
    }

    @Test
    void someoneElsesEntryIs404() {
        when(repo.findByIdAndUserId(9L, 7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.get(7L, 9L)).isInstanceOf(ResourceNotFoundException.class);
    }
}
