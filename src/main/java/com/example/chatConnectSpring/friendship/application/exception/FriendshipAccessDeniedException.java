package com.example.chatConnectSpring.friendship.application.exception;

public class FriendshipAccessDeniedException extends RuntimeException {
    public FriendshipAccessDeniedException(String message) {
        super(message);
    }
}
