package com.example.chatConnectSpring.usuario.infrastructure.adapters.out.persistence.updateEmailToken;

import com.example.chatConnectSpring.usuario.application.mapper.UpdateEmailTokenMapper;
import com.example.chatConnectSpring.usuario.domain.model.UpdateEmailToken;
import com.example.chatConnectSpring.usuario.domain.port.out.UpdateEmailTokenRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class UpdateEmailTokenRepositoryAdapter implements UpdateEmailTokenRepository {
    
    private final UpdateEmailTokenJpaRepository updateEmailTokenJpaRepository;
    
    public UpdateEmailTokenRepositoryAdapter(UpdateEmailTokenJpaRepository updateEmailTokenJpaRepository) {
        this.updateEmailTokenJpaRepository = updateEmailTokenJpaRepository;
    }
    
    @Override
    public void create(UUID userId, String newEmail, String token, LocalDateTime expiresAt) {
        UpdateEmailTokenEntity updateEmailTokenEntity = new UpdateEmailTokenEntity();
        
        updateEmailTokenEntity.setToken(token);
        updateEmailTokenEntity.setExpiresAt(expiresAt);
        updateEmailTokenEntity.setUserId(userId);
        updateEmailTokenEntity.setNewEmail(newEmail);
        
        updateEmailTokenJpaRepository.save(updateEmailTokenEntity);
    }
    
    @Override
    public UpdateEmailToken findByToken(String token) {
        UpdateEmailTokenEntity updateEmailTokenEntity = updateEmailTokenJpaRepository.findByToken(token).orElse(null);
        
        return UpdateEmailTokenMapper.toDomain(updateEmailTokenEntity);
    }
    
    @Override
    public void markAsUsed(UUID id) {
        updateEmailTokenJpaRepository.markAsUsed(id);
    }
}
