package com.example.chatConnectSpring.usuario.domain.port.in;

import java.util.UUID;

public interface UpdatePasswordUseCase {
    void updatePassword(UUID uuid, String oldPassword, String newPassword);
}
