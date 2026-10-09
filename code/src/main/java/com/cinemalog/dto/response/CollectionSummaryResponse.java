package com.cinemalog.dto.response;

import java.time.Instant;
import java.util.List;

public record CollectionSummaryResponse(Long id, String name, String description, int movieCount,
                                        List<MovieSummaryResponse> previewMovies, List<Long> movieIds,
                                        Instant createdAt) {
}
