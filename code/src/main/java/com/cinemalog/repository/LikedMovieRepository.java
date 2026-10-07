package com.cinemalog.repository;

import java.util.List;
import java.util.Optional;

import com.cinemalog.domain.entity.LikedMovie;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LikedMovieRepository extends JpaRepository<LikedMovie, Long> {

    @EntityGraph(attributePaths = "movie")
    List<LikedMovie> findByUserIdOrderByLikedAtDesc(Long userId);

    Optional<LikedMovie> findByUserIdAndMovieId(Long userId, Long movieId);

    boolean existsByUserIdAndMovieId(Long userId, Long movieId);

    long countByUserId(Long userId);

    @Query("select l.movie.id from LikedMovie l where l.user.id = :userId")
    List<Long> findMovieIds(@Param("userId") Long userId);
}
