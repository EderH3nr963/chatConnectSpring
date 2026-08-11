package com.example.chatConnectSpring.usuario.domain.port.out;

import com.example.chatConnectSpring.usuario.domain.model.UpdateEmailToken;

import java.time.LocalDateTime;
import java.util.UUID;

public interface UpdateEmailTokenRepository {
    
    void create(
            UUID userId,
            String newEmail,
            String token,
            LocalDateTime expiresAt
    );
    
    UpdateEmailToken findByToken(String token);
    
    void markAsUsed(UUID id);
}