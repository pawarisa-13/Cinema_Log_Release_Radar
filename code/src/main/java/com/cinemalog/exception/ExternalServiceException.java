package com.cinemalog.exception;

import org.springframework.http.HttpStatus;

public class ExternalServiceException extends ApiException {

    public ExternalServiceException(String message) {
        super(HttpStatus.SERVICE_UNAVAILABLE, message);
    }
}
