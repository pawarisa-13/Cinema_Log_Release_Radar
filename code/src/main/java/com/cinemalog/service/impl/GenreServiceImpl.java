package com.cinemalog.service.impl;

import java.util.List;

import com.cinemalog.dto.response.GenreResponse;
import com.cinemalog.mapper.MovieMapper;
import com.cinemalog.repository.GenreRepository;
import com.cinemalog.service.GenreService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;
    private final MovieMapper movieMapper;

    public GenreServiceImpl(GenreRepository genreRepository, MovieMapper movieMapper) {
        this.genreRepository = genreRepository;
        this.movieMapper = movieMapper;
    }

    @Override
    public List<GenreResponse> listAll() {
        return genreRepository.findAllByOrderByNameAsc().stream().map(movieMapper::toGenre).toList();
    }
}
