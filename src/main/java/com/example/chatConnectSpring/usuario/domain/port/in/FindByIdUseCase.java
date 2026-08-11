package com.example.chatConnectSpring.usuario.domain.port.in;

import com.example.chatConnectSpring.usuario.domain.model.Usuario;

import java.util.UUID;

public interface FindByIdUseCase {
    public Usuario findById(UUID id);
}
