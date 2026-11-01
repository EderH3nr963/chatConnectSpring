package com.example.chatConnectSpring.friendship.application.exception;

public class FriendshipAlreadyExistsException extends RuntimeException {
    public FriendshipAlreadyExistsException(String message) {
        super(message);
    }
}
