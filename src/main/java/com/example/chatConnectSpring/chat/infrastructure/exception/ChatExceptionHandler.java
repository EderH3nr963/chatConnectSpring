package com.example.chatConnectSpring.chat.infrastructure.exception;

import com.example.chatConnectSpring.chat.application.exceptions.ChatAccessDeniedException;
import com.example.chatConnectSpring.chat.application.exceptions.ChatNotFoundException;
import com.example.chatConnectSpring.chat.application.exceptions.InvalidChatException;
import com.example.chatConnectSpring.chat.application.exceptions.ParticipantAccessDeniedException;
import com.example.chatConnectSpring.chat.application.exceptions.ParticipantNotFoundException;
import com.example.chatConnectSpring.shared.exception.ApiError;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ChatExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ChatExceptionHandler.class);
    @ExceptionHandler(ChatNotFoundException.class)
    public ResponseEntity<ApiError> handleChatNotFoundException(ChatNotFoundException ex) {
        log.warn("Chat not found: {}", ex.getMessage());
        return response(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(ParticipantNotFoundException.class)
    public ResponseEntity<ApiError> handleParticipantNotFoundException(ParticipantNotFoundException ex) {
        return response(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(ChatAccessDeniedException.class)
    public ResponseEntity<ApiError> handleChatAccessDeniedException(ChatAccessDeniedException ex) {
        log.warn("Chat access denied: {}", ex.getMessage());
        return response(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(ParticipantAccessDeniedException.class)
    public ResponseEntity<ApiError> handleParticipantAccessDeniedException(ParticipantAccessDeniedException ex) {
        return response(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler({InvalidChatException.class, IllegalArgumentException.class})
    public ResponseEntity<ApiError> handleBadRequest(RuntimeException ex) {
        return response(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    private ResponseEntity<ApiError> response(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new ApiError(status.toString(), message));
    }
}
