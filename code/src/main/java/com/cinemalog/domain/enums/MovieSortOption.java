package com.cinemalog.domain.enums;

import org.springframework.data.domain.Sort;

public enum MovieSortOption {

    POPULARITY(Sort.by(Sort.Order.desc("popularity"))),
    RATING(Sort.by(Sort.Order.desc("voteAverage"), Sort.Order.desc("popularity"))),
    RELEASE_DATE(Sort.by(Sort.Order.desc("releaseDate").nullsLast(), Sort.Order.desc("popularity"))),
    RECENTLY_ADDED(Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id")));

    private final Sort sort;

    MovieSortOption(Sort sort) {
        this.sort = sort;
    }

    public Sort toSort() {
        return sort;
    }
}
