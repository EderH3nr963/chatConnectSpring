package com.example.chatConnectSpring.usuario.domain.port.in;

import com.example.chatConnectSpring.usuario.domain.model.Usuario;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface ConfirmEmailChangeUseCase {
    @Transactional
    Usuario confirmEmailChange(UUID userId, String token);
}
