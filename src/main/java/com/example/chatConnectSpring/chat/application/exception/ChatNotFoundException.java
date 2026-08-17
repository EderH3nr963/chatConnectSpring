package com.example.chatConnectSpring.chat.application.exception;

public class ChatNotFoundException extends RuntimeException {
    public ChatNotFoundException(String message) {
        super(message);
    }
}
