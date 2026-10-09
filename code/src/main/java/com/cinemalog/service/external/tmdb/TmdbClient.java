package com.cinemalog.service.external.tmdb;

import java.util.function.Function;

import com.cinemalog.config.TmdbProperties;
import com.cinemalog.dto.external.tmdb.TmdbGenreListDto;
import com.cinemalog.dto.external.tmdb.TmdbMovieDetailsDto;
import com.cinemalog.dto.external.tmdb.TmdbPageDto;
import com.cinemalog.exception.ExternalServiceException;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriBuilder;

@Component
public class TmdbClient {

    private final RestClient restClient;
    private final TmdbProperties properties;

    public TmdbClient(@Qualifier("tmdbRestClient") RestClient restClient, TmdbProperties properties) {
        this.restClient = restClient;
        this.properties = properties;
    }

    public TmdbPageDto list(String path, int page) {
        return get(b -> b.path(path).queryParam("page", page).queryParam("region", properties.region()),
                TmdbPageDto.class);
    }

    public TmdbPageDto search(String query) {
        return get(b -> b.path("/search/movie").queryParam("query", query).queryParam("include_adult", false),
                TmdbPageDto.class);
    }

    public TmdbMovieDetailsDto details(long tmdbId) {
        return get(b -> b.path("/movie/{id}").queryParam("append_to_response", "credits"), TmdbMovieDetailsDto.class,
                tmdbId);
    }

    public TmdbGenreListDto genres() {
        return get(b -> b.path("/genre/movie/list"), TmdbGenreListDto.class);
    }

    private <T> T get(Function<UriBuilder, UriBuilder> uri, Class<T> type, Object... vars) {
        try {
            return restClient.get()
                    .uri(b -> withCommonParams(uri.apply(b)).build(vars))
                    .retrieve()
                    .body(type);
        } catch (RestClientException ex) {
            throw new ExternalServiceException("TMDB is not reachable right now: " + ex.getMessage());
        }
    }

    private UriBuilder withCommonParams(UriBuilder b) {
        b.queryParam("language", properties.language());
        if (properties.hasApiKey() && !properties.usesBearerToken()) {
            b.queryParam("api_key", properties.apiKey());
        }
        return b;
    }
}
