package com.example.chatConnectSpring.friendship.application.exception;

public class UserIsNotBlockedException extends RuntimeException {
    public UserIsNotBlockedException(String message) {
        super(message);
    }
}
