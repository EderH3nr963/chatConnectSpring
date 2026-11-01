package com.example.chatConnectSpring.auth.infrastructure.adapters.out.persistence.refreshToken;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenEntity, UUID> {
    
    Optional<RefreshTokenEntity> findByTokenHash(String tokenHash);
    
    Optional<RefreshTokenEntity> findById(UUID id);
    
    boolean existsByTokenHash(String tokenHash);

    java.util.List<RefreshTokenEntity> findAllByUserIdAndRevokedAtIsNull(UUID userId);
}
