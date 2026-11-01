package com.example.chatConnectSpring.user.domain.port.in;

import java.util.UUID;

public interface EmailUpdateUseCase {
    void requestEmailUpdate(UUID userId, String newEmail);
    void verifyEmailUpdate(UUID userId, String code);
}
