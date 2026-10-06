package com.cinemalog.service;

import com.cinemalog.domain.entity.Movie;

public interface MovieSyncService {

    void syncGenres();

    int syncCatalog();

    int importSearchResults(String query);

    void refreshDetails(Movie movie);
}
