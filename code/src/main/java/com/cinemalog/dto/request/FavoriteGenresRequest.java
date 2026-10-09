package com.cinemalog.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record FavoriteGenresRequest(
        @NotEmpty(message = "Pick at least one genre") @Size(max = 10) List<Integer> genreIds) {
}
