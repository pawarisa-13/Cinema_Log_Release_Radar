package com.cinemalog.dto.response;

import java.util.List;

public record ReviewListResponse(double average, int ratingCount, List<ReviewResponse> reviews) {
}
