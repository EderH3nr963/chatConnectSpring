package com.example.chatConnectSpring.usuario.application.service;

public class CredenciaisInvalidasException extends RuntimeException {
    public CredenciaisInvalidasException(String message) { super(message); }
}
