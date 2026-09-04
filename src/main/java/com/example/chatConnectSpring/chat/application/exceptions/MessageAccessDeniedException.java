package com.example.chatConnectSpring.chat.application.exceptions;

public class MessageAccessDeniedException extends RuntimeException {
    public MessageAccessDeniedException(String message) {
        super(message);
    }
}
