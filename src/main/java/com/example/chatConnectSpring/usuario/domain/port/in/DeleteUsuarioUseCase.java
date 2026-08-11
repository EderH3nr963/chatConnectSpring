package com.example.chatConnectSpring.usuario.domain.port.in;

import java.util.UUID;

public interface DeleteUsuarioUseCase {
    void delete(UUID id);
}
