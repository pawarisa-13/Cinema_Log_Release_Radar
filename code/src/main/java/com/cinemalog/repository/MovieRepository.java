package com.cinemalog.repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.cinemalog.domain.entity.Movie;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MovieRepository extends JpaRepository<Movie, Long>, JpaSpecificationExecutor<Movie> {

    Optional<Movie> findByTmdbId(Long tmdbId);

    List<Movie> findByReleaseDateBetweenOrderByPopularityDesc(LocalDate from, LocalDate to, Pageable pageable);

    List<Movie> findByReleaseDateAfterOrderByReleaseDateAscPopularityDesc(LocalDate day, Pageable pageable);

    List<Movie> findByReleaseDateLessThanEqualAndVoteAverageGreaterThanEqualOrderByVoteAverageDescPopularityDesc(
            LocalDate day, Double minVote, Pageable pageable);

    @Query("""
            select distinct m from Movie m join m.genres g
            where g.id in :genreIds and m.releaseDate <= :day
            order by m.voteAverage desc, m.popularity desc
            """)
    List<Movie> findReleasedWithAnyGenre(@Param("genreIds") Collection<Integer> genreIds,
            @Param("day") LocalDate day, Pageable pageable);
}
