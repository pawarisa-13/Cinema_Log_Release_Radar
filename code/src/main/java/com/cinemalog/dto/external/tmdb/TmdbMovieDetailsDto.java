package com.cinemalog.dto.external.tmdb;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TmdbMovieDetailsDto(
        Long id,
        String title,
        @JsonProperty("original_title") String originalTitle,
        String overview,
        @JsonProperty("poster_path") String posterPath,
        @JsonProperty("backdrop_path") String backdropPath,
        @JsonProperty("release_date") String releaseDate,
        Integer runtime,
        List<TmdbGenreDto> genres,
        @JsonProperty("vote_average") Double voteAverage,
        Double popularity,
        @JsonProperty("original_language") String originalLanguage,
        TmdbCreditsDto credits) {
}
