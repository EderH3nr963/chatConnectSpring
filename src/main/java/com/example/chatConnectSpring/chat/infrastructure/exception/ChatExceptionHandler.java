package com.example.chatConnectSpring.chat.infrastructure.exception;

import com.example.chatConnectSpring.chat.application.exceptions.ChatAccessDeniedException;
import com.example.chatConnectSpring.chat.application.exceptions.ChatNotFoundException;
import com.example.chatConnectSpring.chat.application.exceptions.InvalidChatException;
import com.example.chatConnectSpring.chat.application.exceptions.ParticipantAccessDeniedException;
import com.example.chatConnectSpring.chat.application.exceptions.ParticipantNotFoundException;
import com.example.chatConnectSpring.shared.exception.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ChatExceptionHandler {
    @ExceptionHandler(ChatNotFoundException.class)
    public ResponseEntity<ApiError> handleChatNotFoundException(ChatNotFoundException ex) {
        return response(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(ParticipantNotFoundException.class)
    public ResponseEntity<ApiError> handleParticipantNotFoundException(ParticipantNotFoundException ex) {
        return response(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(ChatAccessDeniedException.class)
    public ResponseEntity<ApiError> handleChatAccessDeniedException(ChatAccessDeniedException ex) {
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

    private ResponseEntity<com.example.chatConnectSpring.shared.exception.ApiError> response(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new ApiError(status.toString(), message));
    }
}
