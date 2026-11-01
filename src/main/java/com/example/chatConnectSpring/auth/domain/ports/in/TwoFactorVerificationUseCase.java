package com.example.chatConnectSpring.auth.domain.ports.in;

import com.example.chatConnectSpring.auth.application.responses.LoginResponse;

public interface TwoFactorVerificationUseCase {
    LoginResponse verify(String rawEmail, String rawCode);
}
