package com.cinemalog.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.cinemalog.domain.entity.MovieCollection;
import com.cinemalog.domain.entity.User;
import com.cinemalog.dto.request.CollectionRequest;
import com.cinemalog.exception.DuplicateResourceException;
import com.cinemalog.exception.ResourceNotFoundException;
import com.cinemalog.mapper.CollectionMapper;
import com.cinemalog.repository.MovieCollectionRepository;
import com.cinemalog.repository.MovieRepository;
import com.cinemalog.repository.UserRepository;
import com.cinemalog.service.impl.CollectionServiceImpl;

import org.junit.jupiter.api.Test;

class CollectionServiceImplTest {

    MovieCollectionRepository repo = mock(MovieCollectionRepository.class);
    MovieRepository movies = mock(MovieRepository.class);
    UserRepository users = mock(UserRepository.class);
    CollectionMapper mapper = mock(CollectionMapper.class);
    CollectionServiceImpl service = new CollectionServiceImpl(repo, movies, users, mapper);

    @Test
    void duplicateNameIs409() {
        when(repo.existsByUserIdAndNameIgnoreCase(1L, "Halloween Night")).thenReturn(true);

        assertThatThrownBy(() -> service.create(1L, new CollectionRequest(" Halloween Night ", null)))
                .isInstanceOf(DuplicateResourceException.class);
        verify(repo, never()).save(any());
    }

    @Test
    void addingAnUnknownMovieIs404() {
        when(repo.findByIdAndUserId(3L, 1L)).thenReturn(Optional.of(new MovieCollection(mock(User.class), "Rainy Sunday", null)));
        when(movies.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.addMovie(1L, 3L, 99L)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void removingAMovieThatIsNotThereIs404() {
        when(repo.findByIdAndUserId(3L, 1L)).thenReturn(Optional.of(new MovieCollection(mock(User.class), "Rainy Sunday", null)));

        assertThatThrownBy(() -> service.removeMovie(1L, 3L, 5L)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void anotherUsersCollectionIs404() {
        when(repo.findByIdAndUserId(3L, 2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.get(2L, 3L)).isInstanceOf(ResourceNotFoundException.class);
    }
}
