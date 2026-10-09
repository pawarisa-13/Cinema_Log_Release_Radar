package com.cinemalog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.util.List;
import java.util.Optional;

import com.cinemalog.domain.entity.Movie;
import com.cinemalog.domain.enums.MovieSortOption;
import com.cinemalog.dto.request.MovieSearchCriteria;
import com.cinemalog.exception.ExternalServiceException;
import com.cinemalog.exception.ResourceNotFoundException;
import com.cinemalog.mapper.MovieMapper;
import com.cinemalog.repository.MovieRepository;
import com.cinemalog.service.external.MovieCatalogSource;
import com.cinemalog.service.impl.MovieQueryServiceImpl;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

class MovieQueryServiceImplTest {

    MovieRepository repo = mock(MovieRepository.class);
    MovieSyncService sync = mock(MovieSyncService.class);
    MovieCatalogSource source = mock(MovieCatalogSource.class);
    MovieMapper mapper = mock(MovieMapper.class);
    MovieQueryServiceImpl service = new MovieQueryServiceImpl(repo, sync, source, mapper, Clock.systemUTC());

    @SuppressWarnings("unchecked")
    private void emptyResults() {
        when(repo.findAll(any(Specification.class), any(Pageable.class))).thenReturn(new PageImpl<Movie>(List.of()));
    }

    @Test
    void criteriaFillInDefaults() {
        MovieSearchCriteria c = new MovieSearchCriteria(null, null, null, null, null, null, null, null, null, null);
        assertThat(c.page()).isZero();
        assertThat(c.size()).isEqualTo(40);
        assertThat(c.sort()).isEqualTo(MovieSortOption.POPULARITY);
    }

    @Test
    void browsingWithoutTextNeverCallsTmdb() {
        emptyResults();
        service.search(new MovieSearchCriteria(null, 27, null, null, null, null, null, null, 0, 40));
        verify(sync, never()).importSearchResults(anyString());
    }

    @Test
    void textSearchImportsFromTmdbFirstAndSurvivesItsFailure() {
        emptyResults();
        when(source.isAvailable()).thenReturn(true);
        when(sync.importSearchResults("ghibli")).thenThrow(new ExternalServiceException("down"));

        var page = service.search(new MovieSearchCriteria("ghibli", null, null, null, null, null, null, null, 0, 40));

        verify(sync).importSearchResults("ghibli");
        assertThat(page.content()).isEmpty();
    }

    @Test
    void unknownMovieIs404() {
        when(repo.findById(42L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.getDetail(42L)).isInstanceOf(ResourceNotFoundException.class);
    }
}