package com.example.chatConnectSpring.chat.application.exception;

public class ForbbidenChatException extends RuntimeException {
    public ForbbidenChatException(String message) {
        super(message);
    }
}
