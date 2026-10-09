package com.cinemalog.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.cinemalog.exception.ResourceNotFoundException;
import com.cinemalog.mapper.MovieMapper;
import com.cinemalog.repository.MovieRepository;
import com.cinemalog.repository.UserRepository;
import com.cinemalog.repository.WatchlistItemRepository;
import com.cinemalog.service.impl.WatchlistServiceImpl;

import org.junit.jupiter.api.Test;

class WatchlistServiceImplTest {

    WatchlistItemRepository repo = mock(WatchlistItemRepository.class);
    MovieRepository movies = mock(MovieRepository.class);
    WatchlistServiceImpl service = new WatchlistServiceImpl(repo, movies, mock(UserRepository.class), mock(MovieMapper.class));

    @Test
    void addingTwiceIsANoOp() {
        when(repo.existsByUserIdAndMovieId(1L, 5L)).thenReturn(true);
        service.add(1L, 5L);
        verify(repo, never()).save(any());
    }

    @Test
    void addingAnUnknownMovieIs404() {
        when(movies.findById(5L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.add(1L, 5L)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void removingSomethingNotThereDoesNothing() {
        when(repo.findByUserIdAndMovieId(1L, 5L)).thenReturn(Optional.empty());
        service.remove(1L, 5L);
        verify(repo, never()).delete(any());
    }
}
