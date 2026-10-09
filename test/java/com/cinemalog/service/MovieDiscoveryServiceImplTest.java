package com.cinemalog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.cinemalog.domain.entity.Genre;
import com.cinemalog.domain.entity.Movie;
import com.cinemalog.domain.entity.User;
import com.cinemalog.domain.entity.UserProfile;
import com.cinemalog.domain.model.ExternalMovie;
import com.cinemalog.mapper.MovieMapper;
import com.cinemalog.repository.MovieRepository;
import com.cinemalog.repository.UserRepository;
import com.cinemalog.repository.WatchedMovieRepository;
import com.cinemalog.service.impl.MovieDiscoveryServiceImpl;
import com.cinemalog.service.impl.MovieDiscoveryServiceImplAccess;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

class MovieDiscoveryServiceImplTest {

    MovieRepository movies = mock(MovieRepository.class);
    UserRepository users = mock(UserRepository.class);
    WatchedMovieRepository watched = mock(WatchedMovieRepository.class);
    MovieMapper mapper = mock(MovieMapper.class);
    MovieDiscoveryServiceImpl service = new MovieDiscoveryServiceImpl(movies, users, watched, mapper,
            Clock.systemUTC());

    static final Genre HORROR = new Genre(27, "Horror");

    static Movie movie(long id, double vote, Genre... genres) {
        Movie m = new Movie(id, "Movie " + id);
        m.applySummary(
                new ExternalMovie(id, "Movie " + id, null, null, null, null, LocalDate.of(2020, 1, 1), vote, 1.0, "en",
                        List.of(), null, null, null),
                List.of(genres));
        ReflectionTestUtils.setField(m, "id", id);
        return m;
    }

    @Test
    void loggedOutVisitorsGetTopRated() {
        service.recommendedFor(Optional.empty(), 5);
        verify(movies).findByReleaseDateLessThanEqualAndVoteAverageGreaterThanEqualOrderByVoteAverageDescPopularityDesc(
                any(LocalDate.class), anyDouble(), any(Pageable.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    void recommendationsSkipMoviesAlreadyWatched() {
        User user = new User("a@b.c", "x");
        UserProfile profile = new UserProfile("A");
        profile.replaceFavoriteGenres(List.of(HORROR));
        user.attachProfile(profile);
        when(users.findById(1L)).thenReturn(Optional.of(user));
        when(watched.findWatchedMovieIds(1L)).thenReturn(Set.of(10L));
        when(movies.findReleasedWithAnyGenre(anyCollection(), any(LocalDate.class), any(Pageable.class)))
                .thenReturn(List.of(movie(10L, 9.0, HORROR), movie(11L, 7.0, HORROR)));

        service.recommendedFor(Optional.of(1L), 5);

        ArgumentCaptor<List<Movie>> picked = ArgumentCaptor.forClass(List.class);
        verify(mapper).toSummaries(picked.capture());
        assertThat(picked.getValue()).extracting(Movie::getId).containsExactly(11L);
    }

    @Test
    void sameDirectorScoresHigher() {
        Movie a = movie(1L, 7.0, HORROR);
        ReflectionTestUtils.setField(a, "director", "Ari Aster");
        Movie b = movie(2L, 7.0, HORROR);
        assertThat(MovieDiscoveryServiceImplAccess.score(a, Set.of(27), "Ari Aster"))
                .isGreaterThan(MovieDiscoveryServiceImplAccess.score(b, Set.of(27), "Ari Aster"));
    }

    @Test
    void similarExcludesTheMovieItself() {
        Movie self = movie(5L, 8.0, HORROR);
        when(movies.findById(5L)).thenReturn(Optional.of(self));
        when(movies.findReleasedWithAnyGenre(anyCollection(), any(LocalDate.class), any(Pageable.class)))
                .thenReturn(List.of(self, movie(6L, 7.0, HORROR)));

        service.similarTo(5L, 5);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Movie>> picked = ArgumentCaptor.forClass(List.class);
        verify(mapper).toSummaries(picked.capture());
        assertThat(picked.getValue()).extracting(Movie::getId).containsExactly(6L);
        verify(movies).findById(eq(5L));
    }
}