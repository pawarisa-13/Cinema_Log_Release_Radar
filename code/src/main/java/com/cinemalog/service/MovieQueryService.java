package com.cinemalog.service;

import com.cinemalog.dto.request.MovieSearchCriteria;
import com.cinemalog.dto.response.MovieDetailResponse;
import com.cinemalog.dto.response.MovieSummaryResponse;
import com.cinemalog.dto.response.PageResponse;

public interface MovieQueryService {

    PageResponse<MovieSummaryResponse> search(MovieSearchCriteria criteria);

    MovieDetailResponse getDetail(Long movieId);
}
