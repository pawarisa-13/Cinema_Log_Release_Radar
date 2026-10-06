package com.cinemalog.dto.external.tmdb;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TmdbPageDto(Integer page, List<TmdbMovieDto> results, @JsonProperty("total_pages") Integer totalPages) {
}
