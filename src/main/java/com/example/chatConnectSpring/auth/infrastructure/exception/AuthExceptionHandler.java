package com.example.chatConnectSpring.auth.infrastructure.exception;

import com.example.chatConnectSpring.auth.application.exceptions.InvalidCurrentPasswordException;
import com.example.chatConnectSpring.auth.application.exceptions.InvalidVerificationCodeException;
import com.example.chatConnectSpring.auth.domain.exception.InvalidCredentialsException;
import com.example.chatConnectSpring.auth.domain.exception.InvalidRefreshTokenException;
import com.example.chatConnectSpring.auth.domain.exception.InvalidResetCodeException;
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
public class AuthExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(AuthExceptionHandler.class);

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiError> handleInvalidCredentials(InvalidCredentialsException ex) {
        log.warn("Auth failed: invalid credentials - {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiError(HttpStatus.UNAUTHORIZED.toString(), ex.getMessage()));
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ApiError> handleInvalidRefresh(InvalidRefreshTokenException ex) {
        log.warn("Auth failed: invalid refresh token - {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiError(HttpStatus.UNAUTHORIZED.toString(), ex.getMessage()));
    }

    @ExceptionHandler(InvalidResetCodeException.class)
    public ResponseEntity<ApiError> handleInvalidResetCode(InvalidResetCodeException ex) {
        log.warn("Auth failed: invalid reset code - {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiError(HttpStatus.BAD_REQUEST.toString(), ex.getMessage()));
    }

    @ExceptionHandler(InvalidVerificationCodeException.class)
    public ResponseEntity<ApiError> handleInvalidVerificationCode(InvalidVerificationCodeException ex) {
        log.warn("Auth failed: invalid verification code - {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiError(HttpStatus.BAD_REQUEST.toString(), ex.getMessage()));
    }

    @ExceptionHandler(InvalidCurrentPasswordException.class)
    public ResponseEntity<ApiError> handleInvalidCurrentPassword(InvalidCurrentPasswordException ex) {
        log.warn("Auth failed: invalid current password - {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiError(HttpStatus.BAD_REQUEST.toString(), ex.getMessage()));
    }
}
