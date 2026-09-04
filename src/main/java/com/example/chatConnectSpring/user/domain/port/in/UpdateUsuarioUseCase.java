package com.example.chatConnectSpring.user.domain.port.in;

import com.example.chatConnectSpring.user.domain.model.User;

import java.util.UUID;

public interface UpdateUsuarioUseCase {
    User update(UUID id, String username, String email);
}
