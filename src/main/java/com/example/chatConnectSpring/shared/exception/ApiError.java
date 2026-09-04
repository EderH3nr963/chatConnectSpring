package com.example.chatConnectSpring.shared.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(
        String statusCode,
        String message,
        Map<String, String> errors,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime timestamp
) {
    public ApiError(String statusCode, String message) {
        this(statusCode, message, null, LocalDateTime.now());
    }

    public ApiError(String statusCode, String message, Map<String, String> errors) {
        this(statusCode, message, errors, LocalDateTime.now());
    }
}
