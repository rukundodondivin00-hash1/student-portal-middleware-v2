package com.auca.studentportal.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AucaApiException extends RuntimeException {
    private final HttpStatus status;

    public AucaApiException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public AucaApiException(String message, HttpStatus status, Throwable cause) {
        super(message, cause);
        this.status = status;
    }
}
