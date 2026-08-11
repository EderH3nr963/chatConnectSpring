package com.example.chatConnectSpring.usuario.domain.port.in;

import com.example.chatConnectSpring.usuario.domain.model.Usuario;

public interface CreateUsuarioUseCase {
    public Usuario create(String username, String email, String password);
}
