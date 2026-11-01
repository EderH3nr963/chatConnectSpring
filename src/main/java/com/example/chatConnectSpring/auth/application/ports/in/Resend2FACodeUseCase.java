package com.example.chatConnectSpring.auth.application.ports.in;

import java.util.UUID;

public interface Resend2FACodeUseCase {
    void resend(String email);
}
