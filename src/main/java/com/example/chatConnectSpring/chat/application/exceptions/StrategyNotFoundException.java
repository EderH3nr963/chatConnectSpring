package com.example.chatConnectSpring.chat.application.exceptions;

public class StrategyNotFoundException extends RuntimeException {
    public StrategyNotFoundException(String message) {
        super(message);
    }
}
