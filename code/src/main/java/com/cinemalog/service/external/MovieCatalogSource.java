package com.cinemalog.service.external;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.cinemalog.domain.enums.MovieListType;
import com.cinemalog.domain.model.ExternalMovie;

public interface MovieCatalogSource {

    boolean isAvailable();

    List<ExternalMovie> fetchList(MovieListType type, int page);

    List<ExternalMovie> search(String query);

    Optional<ExternalMovie> fetchDetails(long externalId);

    Map<Integer, String> fetchGenres();
}
