package com.example.chatConnectSpring.auth.application.ports.in;

import java.util.UUID;

public interface UpdatePasswordUseCase {
    void updatePassword(UUID userId, String currentPassword, String newPassword);
}
