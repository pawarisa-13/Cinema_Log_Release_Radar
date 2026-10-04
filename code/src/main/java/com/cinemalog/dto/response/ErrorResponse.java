package com.cinemalog.dto.response;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(Instant timestamp, int status, String error, String message, String path,
                            List<FieldErrorItem> fieldErrors) {

    public record FieldErrorItem(String field, String message) {
    }
}
