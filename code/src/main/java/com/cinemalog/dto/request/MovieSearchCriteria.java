package com.cinemalog.dto.request;

import com.cinemalog.domain.enums.MovieSortOption;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record MovieSearchCriteria(
        @Size(max = 100) String q,
        Integer genreId,
        @Min(1874) @Max(2100) Integer year,
        @Min(1870) @Max(2100) Integer decade,
        @Min(1874) @Max(2100) Integer before,
        @DecimalMin("0.0") @DecimalMax("10.0") Double minRating,
        @Size(max = 10) String language,
        MovieSortOption sort,
        @Min(0) Integer page,
        @Min(1) @Max(100) Integer size) {

    public MovieSearchCriteria {
        sort = sort == null ? MovieSortOption.POPULARITY : sort;
        page = page == null ? 0 : page;
        size = size == null ? 40 : size;
        q = q == null ? null : q.trim();
    }

    public boolean hasText() {
        return q != null && q.length() >= 2;
    }
}
