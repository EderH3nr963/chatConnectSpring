package com.example.chatConnectSpring.auth.infrastructure.mappers;

import com.example.chatConnectSpring.auth.domain.model.RefreshToken;
import com.example.chatConnectSpring.auth.domain.model.vo.RefreshTokenHash;
import com.example.chatConnectSpring.auth.infrastructure.adapters.out.persistence.refreshToken.RefreshTokenEntity;

public class RefreshTokenMapper {

    private RefreshTokenMapper() {}

    public static RefreshTokenEntity toEntity(RefreshToken domain) {
        RefreshTokenEntity entity = new RefreshTokenEntity();
        entity.setId(domain.id());
        entity.setTokenHash(domain.tokenHash().value());
        entity.setUserId(domain.userId());
        entity.setExpiresAt(domain.expiresAt());
        entity.setRevokedAt(domain.revokedAt());
        entity.setCreatedAt(domain.createdAt());
        return entity;
    }

    public static RefreshToken toDomain(RefreshTokenEntity entity) {
        return RefreshToken.reconstitute(
                entity.getId(),
                RefreshTokenHash.of(entity.getTokenHash()),
                entity.getUserId(),
                entity.getExpiresAt(),
                entity.getRevokedAt(),
                entity.getCreatedAt()
        );
    }
}
