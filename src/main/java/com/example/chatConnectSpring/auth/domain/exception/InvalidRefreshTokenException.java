package com.example.chatConnectSpring.auth.domain.exception;

import org.springframework.security.authentication.BadCredentialsException;

public class InvalidRefreshTokenException extends BadCredentialsException {

    public InvalidRefreshTokenException() {
        super("Invalid refresh token");
    }
}
