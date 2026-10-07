package com.cinemalog.service;

import java.util.List;

import com.cinemalog.dto.request.CollectionRequest;
import com.cinemalog.dto.response.CollectionDetailResponse;
import com.cinemalog.dto.response.CollectionSummaryResponse;

public interface CollectionService {

    List<CollectionSummaryResponse> list(Long userId);

    CollectionDetailResponse get(Long userId, Long collectionId);

    CollectionDetailResponse create(Long userId, CollectionRequest request);

    CollectionDetailResponse update(Long userId, Long collectionId, CollectionRequest request);

    void delete(Long userId, Long collectionId);

    CollectionDetailResponse addMovie(Long userId, Long collectionId, Long movieId);

    CollectionDetailResponse removeMovie(Long userId, Long collectionId, Long movieId);
}
