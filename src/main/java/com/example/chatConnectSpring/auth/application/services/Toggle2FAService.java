package com.example.chatConnectSpring.auth.application.services;

import com.example.chatConnectSpring.auth.application.ports.in.Toggle2FAUseCase;
import com.example.chatConnectSpring.user.domain.model.User;
import com.example.chatConnectSpring.user.domain.port.out.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class Toggle2FAService implements Toggle2FAUseCase {

    private final UsuarioRepository usuarioRepository;

    public Toggle2FAService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional
    public void toggle(UUID userId, boolean active) {
        User user = usuarioRepository.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found: " + userId);
        }
        user.setActive2fa(active);
        usuarioRepository.save(user);
    }
}
