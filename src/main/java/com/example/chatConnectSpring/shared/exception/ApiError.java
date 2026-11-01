package com.example.chatConnectSpring.shared.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.OffsetDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(
        String statusCode,
        String message,
        Map<String, String> errors,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
        OffsetDateTime timestamp
) {
    public ApiError(String statusCode, String message) {
        this(statusCode, message, null, OffsetDateTime.now());
    }

    public ApiError(String statusCode, String message, Map<String, String> errors) {
        this(statusCode, message, errors, OffsetDateTime.now());
    }
}
