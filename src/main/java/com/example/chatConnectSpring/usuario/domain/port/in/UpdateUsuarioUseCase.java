package com.example.chatConnectSpring.usuario.domain.port.in;

import com.example.chatConnectSpring.usuario.domain.model.Usuario;

import java.util.UUID;

public interface UpdateUsuarioUseCase {
    Usuario update(UUID id, String username, String email);
}
