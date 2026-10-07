package com.cinemalog.dto.response;

import java.time.Instant;
import java.util.List;

public record CollectionDetailResponse(Long id, String name, String description, List<MovieSummaryResponse> movies,
                                       Instant createdAt) {
}
