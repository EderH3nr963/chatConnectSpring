package com.example.chatConnectSpring.auth.application.ports.in;

import java.util.UUID;

public interface Toggle2FAUseCase {
    void toggle(UUID userId, boolean active);
}
