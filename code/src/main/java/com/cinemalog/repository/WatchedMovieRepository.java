package com.cinemalog.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.cinemalog.domain.entity.WatchedMovie;
import com.cinemalog.domain.enums.WatchPlace;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WatchedMovieRepository extends JpaRepository<WatchedMovie, Long> {

    interface GenreCount {
        String getName();

        Long getTotal();
    }

    @EntityGraph(attributePaths = "movie")
    Page<WatchedMovie> findByUserId(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = "movie")
    List<WatchedMovie> findByUserIdAndWatchedDateBetweenOrderByWatchedDateAscIdAsc(Long userId, LocalDate from, LocalDate to);

    @EntityGraph(attributePaths = "movie")
    Optional<WatchedMovie> findByIdAndUserId(Long id, Long userId);

    List<WatchedMovie> findByUserIdOrderByWatchedDateAscIdAsc(Long userId);

    @Query("""
            select w from WatchedMovie w join fetch w.user u
            where w.movie.id = :movieId and (w.review is not null or w.rating is not null)
            order by w.watchedDate desc, w.id desc
            """)
    List<WatchedMovie> findReviewsForMovie(@Param("movieId") Long movieId, Pageable pageable);

    @Query("select distinct w.movie.id from WatchedMovie w where w.user.id = :userId")
    Set<Long> findWatchedMovieIds(@Param("userId") Long userId);

    long countByUserId(Long userId);

    long countByUserIdAndReviewIsNotNull(Long userId);

    long countByUserIdAndPlace(Long userId, WatchPlace place);

    @Query("select count(distinct w.movie.id) from WatchedMovie w where w.user.id = :userId")
    long countDistinctMovies(@Param("userId") Long userId);

    @Query("select coalesce(avg(w.rating), 0.0) from WatchedMovie w where w.user.id = :userId and w.rating is not null")
    Double averageRating(@Param("userId") Long userId);

    @Query("""
            select g.name as name, count(distinct m.id) as total
            from WatchedMovie w join w.movie m join m.genres g
            where w.user.id = :userId
            group by g.name
            order by count(distinct m.id) desc, g.name asc
            """)
    List<GenreCount> countGenres(@Param("userId") Long userId, Pageable pageable);
}
