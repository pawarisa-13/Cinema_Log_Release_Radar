package com.cinemalog.dto.external.tmdb;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TmdbCreditsDto(List<Person> cast, List<Person> crew) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Person(String name, String job, Integer order) {
    }
}
