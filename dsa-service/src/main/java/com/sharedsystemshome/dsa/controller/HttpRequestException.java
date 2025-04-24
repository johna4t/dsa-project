package com.sharedsystemshome.dsa.controller;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class HttpRequestException {

    private final String timestamp;

    private final int status;

    private final String error;

    private final String message;

    public HttpRequestException(int status, String error, String message) {
        this.timestamp = ZonedDateTime.now(ZoneId.of("Z")).toLocalDateTime().toString();
        this.status = status;
        this.error = error;
        this.message = message;
    }

    public HttpRequestException(HttpStatus httpStatus, String message) {
        this.timestamp = ZonedDateTime.now(ZoneId.of("Z")).toString();
        this.status = httpStatus.value();
        this.error = httpStatus.getReasonPhrase();
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }
}
