package com.cinemalog.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.cinemalog.repository.specification.MovieSpecificationBuilder;
import com.cinemalog.repository.specification.MovieSpecificationBuilderAccess;

import org.junit.jupiter.api.Test;

class MovieSpecificationBuilderTest {

    @Test
    void blankAndNullFiltersAreSkipped() {
        MovieSpecificationBuilder b = MovieSpecificationBuilder.create()
                .text("  ").genre(null).year(null).decade(null).releasedBefore(null).minRating(0.0).language("");
        assertThat(MovieSpecificationBuilderAccess.size(b)).isZero();
        assertThat(b.build()).isNotNull();
    }

    @Test
    void everyChosenFilterAddsOneCondition() {
        MovieSpecificationBuilder b = MovieSpecificationBuilder.create()
                .text("ghost").genre(27).decade(2014).minRating(7.0).language("other");
        assertThat(MovieSpecificationBuilderAccess.size(b)).isEqualTo(5);
    }
}
