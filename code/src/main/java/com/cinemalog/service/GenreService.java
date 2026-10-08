package com.cinemalog.service;

import java.util.List;

import com.cinemalog.dto.response.GenreResponse;

public interface GenreService {

    List<GenreResponse> listAll();
}
