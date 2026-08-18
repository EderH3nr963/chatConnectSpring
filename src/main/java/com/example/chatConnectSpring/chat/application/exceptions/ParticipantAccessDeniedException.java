package com.example.chatConnectSpring.chat.application.exceptions;

public class ParticipantAccessDeniedException extends RuntimeException {
    public ParticipantAccessDeniedException(String message) {
        super(message);
    }
}
