package com.example.chatConnectSpring.chat.application.exceptions;

public class InvalidChatException extends RuntimeException {
    public InvalidChatException(String message) {
        super(message);
    }
}
