package com.example.chatConnectSpring.usuario.application.mapper;

import com.example.chatConnectSpring.usuario.domain.model.UpdateEmailToken;
import com.example.chatConnectSpring.usuario.infrastructure.adapters.out.persistence.updateEmailToken.UpdateEmailTokenEntity;

public class UpdateEmailTokenMapper {
    public static UpdateEmailToken toDomain(UpdateEmailTokenEntity entity) {
        UpdateEmailToken domain = new UpdateEmailToken();
        
        domain.setId(entity.getId());
        domain.setToken(entity.getToken());
        domain.setExpiresAt(entity.getExpiresAt());
        domain.setNewEmail(entity.getNewEmail());
        domain.setUserId(entity.getUserId());
        domain.setUsed(entity.isUsed());
        
        return domain;
    }

}
