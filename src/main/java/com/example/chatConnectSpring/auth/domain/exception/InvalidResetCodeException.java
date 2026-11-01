package com.example.chatConnectSpring.auth.domain.exception;

public class InvalidResetCodeException extends RuntimeException {

    public InvalidResetCodeException(String message) {
        super(message);
    }
}
