package com.cinemalog.mapper;

import java.util.List;

import com.cinemalog.domain.entity.CollectionMovie;
import com.cinemalog.domain.entity.Movie;
import com.cinemalog.domain.entity.MovieCollection;
import com.cinemalog.dto.response.CollectionDetailResponse;
import com.cinemalog.dto.response.CollectionSummaryResponse;

import org.springframework.stereotype.Component;

@Component
public class CollectionMapper {

    private static final int PREVIEW_SIZE = 3;

    private final MovieMapper movieMapper;

    public CollectionMapper(MovieMapper movieMapper) {
        this.movieMapper = movieMapper;
    }

    public CollectionSummaryResponse toSummary(MovieCollection c) {
        List<Movie> movies = movies(c);
        return new CollectionSummaryResponse(c.getId(), c.getName(), c.getDescription(), movies.size(),
                movieMapper.toSummaries(movies.stream().limit(PREVIEW_SIZE).toList()),
                movies.stream().map(Movie::getId).toList(), c.getCreatedAt());
    }

    public CollectionDetailResponse toDetail(MovieCollection c) {
        return new CollectionDetailResponse(c.getId(), c.getName(), c.getDescription(),
                movieMapper.toSummaries(movies(c)), c.getCreatedAt());
    }

    private static List<Movie> movies(MovieCollection c) {
        return c.getItems().stream().map(CollectionMovie::getMovie).toList();
    }
}
