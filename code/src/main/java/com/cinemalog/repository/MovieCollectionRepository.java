package com.cinemalog.repository;

import java.util.List;
import java.util.Optional;

import com.cinemalog.domain.entity.MovieCollection;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieCollectionRepository extends JpaRepository<MovieCollection, Long> {

    List<MovieCollection> findByUserIdOrderByCreatedAtAscIdAsc(Long userId);

    Optional<MovieCollection> findByIdAndUserId(Long id, Long userId);

    boolean existsByUserIdAndNameIgnoreCase(Long userId, String name);

    boolean existsByUserIdAndNameIgnoreCaseAndIdNot(Long userId, String name, Long id);

    long countByUserId(Long userId);
}
