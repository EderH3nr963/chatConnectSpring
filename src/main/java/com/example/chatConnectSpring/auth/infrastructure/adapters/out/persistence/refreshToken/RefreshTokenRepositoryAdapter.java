package com.example.chatConnectSpring.auth.infrastructure.adapters.out.persistence.refreshToken;

import com.example.chatConnectSpring.auth.domain.model.RefreshToken;
import com.example.chatConnectSpring.auth.domain.model.vo.RefreshTokenHash;
import com.example.chatConnectSpring.auth.domain.ports.out.RefreshTokenRepository;
import com.example.chatConnectSpring.auth.infrastructure.mappers.RefreshTokenMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Component
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepository {

    private final RefreshTokenJpaRepository repository;

    public RefreshTokenRepositoryAdapter(RefreshTokenJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        RefreshTokenEntity entity = RefreshTokenMapper.toEntity(refreshToken);
        RefreshTokenEntity saved = repository.save(entity);
        return RefreshTokenMapper.toDomain(saved);
    }

    @Override
    public Optional<RefreshToken> findByTokenHash(RefreshTokenHash tokenHash) {
        return repository.findByTokenHash(tokenHash.value())
                .map(RefreshTokenMapper::toDomain);
    }

    @Override
    public Optional<RefreshToken> findById(UUID id) {
        return repository.findById(id)
                .map(RefreshTokenMapper::toDomain);
    }

    @Override
    @Transactional
    public void revoke(UUID id) {
        repository.findById(id).ifPresent(this::markRevoked);
    }

    @Override
    @Transactional
    public void revokeAllByUserId(UUID userId) {
        repository.findAllByUserIdAndRevokedAtIsNull(userId).forEach(this::markRevoked);
    }

    @Override
    @Transactional
    public void revokeByTokenHash(RefreshTokenHash tokenHash) {
        repository.findByTokenHash(tokenHash.value()).ifPresent(this::markRevoked);
    }

    @Override
    public boolean existsByTokenHash(RefreshTokenHash tokenHash) {
        return repository.existsByTokenHash(tokenHash.value());
    }

    private void markRevoked(RefreshTokenEntity entity) {
        entity.setRevokedAt(OffsetDateTime.now());
        repository.save(entity);
    }
}
