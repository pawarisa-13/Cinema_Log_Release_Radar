package com.cinemalog.service.external;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.cinemalog.config.TmdbProperties;
import com.cinemalog.domain.enums.MovieListType;
import com.cinemalog.domain.model.ExternalMovie;
import com.cinemalog.dto.external.tmdb.TmdbCreditsDto;
import com.cinemalog.dto.external.tmdb.TmdbGenreDto;
import com.cinemalog.dto.external.tmdb.TmdbMovieDetailsDto;
import com.cinemalog.dto.external.tmdb.TmdbMovieDto;
import com.cinemalog.dto.external.tmdb.TmdbPageDto;
import com.cinemalog.service.external.tmdb.TmdbClient;
import com.cinemalog.service.external.tmdb.TmdbMovieCatalogAdapter;

import org.junit.jupiter.api.Test;

class TmdbMovieCatalogAdapterTest {

    TmdbClient client = mock(TmdbClient.class);
    TmdbProperties props = new TmdbProperties("https://api.themoviedb.org/3", "https://image.tmdb.org/t/p",
            "0123456789abcdef0123456789abcdef", "en-US", "TH", 1, "0 0 * * * *");
    TmdbMovieCatalogAdapter adapter = new TmdbMovieCatalogAdapter(client, props);

    @Test
    void convertsAListPageAndHandlesEmptyDates() {
        when(client.list("/movie/upcoming", 1)).thenReturn(new TmdbPageDto(1, List.of(
                new TmdbMovieDto(1L, "Dune: Part Three", "Dune: Part Three", "…", "/p.jpg", null, "2026-12-18",
                        List.of(878), 0.0, 97.0, "en"),
                new TmdbMovieDto(2L, "Untitled", null, null, null, null, "", List.of(), 0.0, 1.0, "en")), 1));

        List<ExternalMovie> movies = adapter.fetchList(MovieListType.UPCOMING, 1);

        assertThat(movies).hasSize(2);
        assertThat(movies.get(0).releaseDate()).isEqualTo(LocalDate.of(2026, 12, 18));
        assertThat(movies.get(0).genreIds()).containsExactly(878);
        assertThat(movies.get(1).releaseDate()).isNull();
    }

    @Test
    void detailsPickTheDirectorAndTopBilledCastInOrder() {
        TmdbCreditsDto credits = new TmdbCreditsDto(
                List.of(new TmdbCreditsDto.Person("Mone Kamishiraishi", null, 1),
                        new TmdbCreditsDto.Person("Ryunosuke Kamiki", null, 0)),
                List.of(new TmdbCreditsDto.Person("Someone", "Producer", null),
                        new TmdbCreditsDto.Person("Makoto Shinkai", "Director", null)));
        when(client.details(372058L))
                .thenReturn(new TmdbMovieDetailsDto(372058L, "Your Name.", "君の名は。", "…", "/p.jpg", "/b.jpg",
                        "2016-08-26", 106, List.of(new TmdbGenreDto(16, "Animation")), 8.5, 85.0, "ja", credits));

        Optional<ExternalMovie> m = adapter.fetchDetails(372058L);

        assertThat(m).isPresent();
        assertThat(m.get().director()).isEqualTo("Makoto Shinkai");
        assertThat(m.get().cast()).containsExactly("Ryunosuke Kamiki", "Mone Kamishiraishi");
        assertThat(m.get().runtime()).isEqualTo(106);
        assertThat(m.get().genreIds()).containsExactly(16);
    }

    @Test
    void isAvailableOnlyWithAnApiKey() {
        TmdbProperties noKey = new TmdbProperties("u", "i", "", "en-US", "TH", 1, "c");
        assertThat(new TmdbMovieCatalogAdapter(client, noKey).isAvailable()).isFalse();
        assertThat(adapter.isAvailable()).isTrue();
        assertThat(props.usesBearerToken()).isFalse();
    }
}
