package com.cinemalog.repository;

import java.util.List;

import com.cinemalog.domain.entity.Genre;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<Genre, Integer> {

    List<Genre> findAllByOrderByNameAsc();
}
