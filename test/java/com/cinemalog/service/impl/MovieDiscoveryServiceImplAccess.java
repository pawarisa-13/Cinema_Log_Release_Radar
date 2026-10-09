package com.cinemalog.service.impl;

import java.util.Set;

import com.cinemalog.domain.entity.Movie;

public final class MovieDiscoveryServiceImplAccess {

    private MovieDiscoveryServiceImplAccess() {
    }

    public static double score(Movie m, Set<Integer> genreIds, String director) {
        return MovieDiscoveryServiceImpl.score(m, genreIds, director);
    }
}