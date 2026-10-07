package com.cinemalog.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CollectionRequest(
        @NotBlank(message = "Give your collection a name") @Size(max = 60) String name,
        @Size(max = 200) String description) {
}
