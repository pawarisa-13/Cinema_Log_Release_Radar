package com.cinemalog.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.cinemalog.domain.entity.Movie;
import com.cinemalog.domain.entity.MovieCollection;
import com.cinemalog.domain.entity.User;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class MovieCollectionTest {

    static Movie movie(long id) {
        Movie m = new Movie(id, "Movie " + id);
        ReflectionTestUtils.setField(m, "id", id);
        return m;
    }

    @Test
    void addingTheSameMovieTwiceKeepsOneCopy() {
        MovieCollection c = new MovieCollection(mock(User.class), "Ghibli Weekend", null);
        Movie totoro = movie(1L);

        assertThat(c.addMovie(totoro)).isTrue();
        assertThat(c.addMovie(totoro)).isFalse();
        assertThat(c.getItems()).hasSize(1);
    }

    @Test
    void newestMovieComesFirstAndRemoveWorks() {
        MovieCollection c = new MovieCollection(mock(User.class), "Halloween Night", "  ");
        c.addMovie(movie(1L));
        c.addMovie(movie(2L));

        assertThat(c.getItems().get(0).getMovie().getId()).isEqualTo(2L);
        assertThat(c.getDescription()).isNull();
        assertThat(c.removeMovie(1L)).isTrue();
        assertThat(c.removeMovie(1L)).isFalse();
    }
}
