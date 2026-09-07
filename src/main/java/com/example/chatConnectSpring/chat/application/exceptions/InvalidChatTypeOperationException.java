package com.example.chatConnectSpring.chat.application.exceptions;

public class InvalidChatTypeOperationException extends RuntimeException {
    public InvalidChatTypeOperationException(String message) {
        super(message);
    }
}
