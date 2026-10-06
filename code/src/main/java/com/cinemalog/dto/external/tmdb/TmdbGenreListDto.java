package com.cinemalog.dto.external.tmdb;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TmdbGenreListDto(List<TmdbGenreDto> genres) {
}
