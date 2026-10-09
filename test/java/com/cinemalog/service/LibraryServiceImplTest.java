package com.cinemalog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import com.cinemalog.domain.entity.Movie;
import com.cinemalog.domain.entity.User;
import com.cinemalog.domain.entity.WatchedMovie;
import com.cinemalog.domain.enums.WatchPlace;
import com.cinemalog.dto.response.LibraryStateResponse;
import com.cinemalog.repository.LikedMovieRepository;
import com.cinemalog.repository.MovieCollectionRepository;
import com.cinemalog.repository.WatchedMovieRepository;
import com.cinemalog.repository.WatchlistItemRepository;
import com.cinemalog.service.impl.LibraryServiceImpl;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class LibraryServiceImplTest {

    WatchedMovieRepository watched = mock(WatchedMovieRepository.class);
    LibraryServiceImpl service = new LibraryServiceImpl(watched, mock(WatchlistItemRepository.class),
            mock(LikedMovieRepository.class), mock(MovieCollectionRepository.class),
            mock(ReminderService.class), mock(NotificationService.class));

    @Test
    void stateKeepsOnlyTheLatestEntryPerMovie() {
        Movie m = new Movie(1L, "Spirited Away");
        ReflectionTestUtils.setField(m, "id", 1L);
        WatchedMovie first = new WatchedMovie(mock(User.class), m, LocalDate.of(2026, 1, 1), 4, null, WatchPlace.HOME);
        WatchedMovie rewatch = new WatchedMovie(mock(User.class), m, LocalDate.of(2026, 9, 5), 5, "again!", WatchPlace.HOME);
        ReflectionTestUtils.setField(first, "id", 10L);
        ReflectionTestUtils.setField(rewatch, "id", 11L);
        when(watched.findByUserIdOrderByWatchedDateAscIdAsc(7L)).thenReturn(List.of(first, rewatch));

        LibraryStateResponse state = service.state(7L);

        assertThat(state.watched()).hasSize(1);
        assertThat(state.watched().get(0).entryId()).isEqualTo(11L);
        assertThat(state.watched().get(0).reviewed()).isTrue();
    }
}
