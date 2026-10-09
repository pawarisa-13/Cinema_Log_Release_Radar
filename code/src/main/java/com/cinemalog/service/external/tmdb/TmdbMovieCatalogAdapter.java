package com.cinemalog.service.external.tmdb;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.cinemalog.config.TmdbProperties;
import com.cinemalog.domain.enums.MovieListType;
import com.cinemalog.domain.model.ExternalMovie;
import com.cinemalog.dto.external.tmdb.TmdbCreditsDto;
import com.cinemalog.dto.external.tmdb.TmdbGenreDto;
import com.cinemalog.dto.external.tmdb.TmdbMovieDetailsDto;
import com.cinemalog.dto.external.tmdb.TmdbMovieDto;
import com.cinemalog.dto.external.tmdb.TmdbPageDto;
import com.cinemalog.service.external.MovieCatalogSource;

import org.springframework.stereotype.Component;

@Component
public class TmdbMovieCatalogAdapter implements MovieCatalogSource {

    private static final int MAX_CAST = 6;
    private static final Map<MovieListType, String> PATHS = new EnumMap<>(Map.of(
            MovieListType.NOW_PLAYING, "/movie/now_playing",
            MovieListType.UPCOMING, "/movie/upcoming",
            MovieListType.POPULAR, "/movie/popular",
            MovieListType.TOP_RATED, "/movie/top_rated"));

    private final TmdbClient client;
    private final TmdbProperties properties;

    public TmdbMovieCatalogAdapter(TmdbClient client, TmdbProperties properties) {
        this.client = client;
        this.properties = properties;
    }

    @Override
    public boolean isAvailable() {
        return properties.hasApiKey();
    }

    @Override
    public List<ExternalMovie> fetchList(MovieListType type, int page) {
        return fromPage(client.list(PATHS.get(type), page));
    }

    @Override
    public List<ExternalMovie> search(String query) {
        return fromPage(client.search(query));
    }

    @Override
    public Optional<ExternalMovie> fetchDetails(long externalId) {
        return Optional.ofNullable(client.details(externalId)).map(TmdbMovieCatalogAdapter::fromDetails);
    }

    @Override
    public Map<Integer, String> fetchGenres() {
        Map<Integer, String> out = new LinkedHashMap<>();
        var list = client.genres();
        if (list != null && list.genres() != null) {
            list.genres().forEach(g -> out.put(g.id(), g.name()));
        }
        return out;
    }

    private static List<ExternalMovie> fromPage(TmdbPageDto page) {
        if (page == null || page.results() == null) {
            return List.of();
        }
        return page.results().stream().filter(m -> m.id() != null && m.title() != null)
                .map(TmdbMovieCatalogAdapter::fromSummary).toList();
    }

    static ExternalMovie fromSummary(TmdbMovieDto m) {
        return new ExternalMovie(m.id(), m.title(), m.originalTitle(), m.overview(), m.posterPath(), m.backdropPath(),
                parseDate(m.releaseDate()), m.voteAverage(), m.popularity(), m.originalLanguage(),
                m.genreIds() == null ? List.of() : m.genreIds(), null, null, null);
    }

    static ExternalMovie fromDetails(TmdbMovieDetailsDto d) {
        TmdbCreditsDto credits = d.credits();
        String director = credits == null || credits.crew() == null ? null
                : credits.crew().stream()
                        .filter(p -> "Director".equals(p.job())).map(TmdbCreditsDto.Person::name).findFirst()
                        .orElse(null);
        List<String> cast = credits == null || credits.cast() == null ? List.of()
                : credits.cast().stream()
                        .sorted(Comparator.comparing(
                                (TmdbCreditsDto.Person p) -> p.order() == null ? Integer.MAX_VALUE : p.order()))
                        .map(TmdbCreditsDto.Person::name).filter(Objects::nonNull).limit(MAX_CAST).toList();
        List<Integer> genreIds = d.genres() == null ? List.of() : d.genres().stream().map(TmdbGenreDto::id).toList();
        return new ExternalMovie(d.id(), d.title(), d.originalTitle(), d.overview(), d.posterPath(), d.backdropPath(),
                parseDate(d.releaseDate()), d.voteAverage(), d.popularity(), d.originalLanguage(), genreIds,
                d.runtime(), director, cast);
    }

    static LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException ex) {
            return null;
        }
    }
}
