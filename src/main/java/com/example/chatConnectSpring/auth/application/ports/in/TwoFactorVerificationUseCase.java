package com.example.chatConnectSpring.auth.application.ports.in;

public interface TwoFactorVerificationUseCase {
    com.example.chatConnectSpring.auth.application.responses.LoginResponse verify(String email, String code);
}
