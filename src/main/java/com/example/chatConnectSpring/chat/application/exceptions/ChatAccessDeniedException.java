package com.example.chatConnectSpring.chat.application.exceptions;

public class ChatAccessDeniedException extends RuntimeException {
    public ChatAccessDeniedException(String message) {
        super(message);
    }
}
