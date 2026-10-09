package com.cinemalog.repository;

import java.util.List;
import java.util.Optional;

import com.cinemalog.domain.entity.WatchlistItem;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WatchlistItemRepository extends JpaRepository<WatchlistItem, Long> {

    @EntityGraph(attributePaths = "movie")
    List<WatchlistItem> findByUserIdOrderByAddedAtDesc(Long userId);

    Optional<WatchlistItem> findByUserIdAndMovieId(Long userId, Long movieId);

    boolean existsByUserIdAndMovieId(Long userId, Long movieId);

    long countByUserId(Long userId);

    @Query("select w.movie.id from WatchlistItem w where w.user.id = :userId")
    List<Long> findMovieIds(@Param("userId") Long userId);
}
