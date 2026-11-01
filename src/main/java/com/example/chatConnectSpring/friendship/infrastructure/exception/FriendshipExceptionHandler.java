package com.example.chatConnectSpring.friendship.infrastructure.exception;

import com.example.chatConnectSpring.friendship.application.exception.FriendshipAccessDeniedException;
import com.example.chatConnectSpring.friendship.application.exception.FriendshipAlreadyExistsException;
import com.example.chatConnectSpring.friendship.application.exception.FriendshipNotFoundException;
import com.example.chatConnectSpring.friendship.application.exception.InvalidFriendshipException;
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
public class FriendshipExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(FriendshipExceptionHandler.class);

    @ExceptionHandler(FriendshipNotFoundException.class)
    public ResponseEntity<ApiError> handleFriendshipNotFoundException(FriendshipNotFoundException ex) {
        log.warn("Friendship not found: {}", ex.getMessage());
        return response(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(FriendshipAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleFriendshipAlreadyExistsException(FriendshipAlreadyExistsException ex) {
        log.warn("Friendship already exists: {}", ex.getMessage());
        return response(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(FriendshipAccessDeniedException.class)
    public ResponseEntity<ApiError> handleFriendshipAccessDeniedException(FriendshipAccessDeniedException ex) {
        log.warn("Friendship access denied: {}", ex.getMessage());
        return response(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(InvalidFriendshipException.class)
    public ResponseEntity<ApiError> handleInvalidFriendshipException(InvalidFriendshipException ex) {
        log.warn("Invalid friendship: {}", ex.getMessage());
        return response(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    private ResponseEntity<ApiError> response(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new ApiError(status.toString(), message));
    }
}
