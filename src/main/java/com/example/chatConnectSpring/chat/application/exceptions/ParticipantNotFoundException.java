package com.example.chatConnectSpring.chat.application.exceptions;

public class ParticipantNotFoundException extends RuntimeException {
    public ParticipantNotFoundException(String message) {
        super(message);
    }
}
