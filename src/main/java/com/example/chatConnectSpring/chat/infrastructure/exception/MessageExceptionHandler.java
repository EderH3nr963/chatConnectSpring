package com.example.chatConnectSpring.chat.infrastructure.exception;

import com.example.chatConnectSpring.chat.application.exceptions.InvalidMessageException;
import com.example.chatConnectSpring.chat.application.exceptions.MessageAccessDeniedException;
import com.example.chatConnectSpring.chat.application.exceptions.MessageNotFoundException;
import com.example.chatConnectSpring.shared.exception.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class MessageExceptionHandler {

    @ExceptionHandler(MessageNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(MessageNotFoundException ex) {
        return response(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(MessageAccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(MessageAccessDeniedException ex) {
        return response(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(InvalidMessageException.class)
    public ResponseEntity<ApiError> handleInvalidMessage(InvalidMessageException ex) {
        return response(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    private ResponseEntity<ApiError> response(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new ApiError(status.toString(), message));
    }
}
