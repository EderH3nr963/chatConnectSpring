package com.example.chatConnectSpring.usuario.domain.port.in;

import com.example.chatConnectSpring.usuario.domain.model.Usuario;

public interface FindByEmailUseCase {
    public Usuario findByEmail(String email);
}
