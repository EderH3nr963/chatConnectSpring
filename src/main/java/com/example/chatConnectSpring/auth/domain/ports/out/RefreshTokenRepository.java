package com.example.chatConnectSpring.auth.domain.ports.out;

import com.example.chatConnectSpring.auth.domain.model.RefreshToken;
import com.example.chatConnectSpring.auth.domain.model.vo.RefreshTokenHash;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository {

    RefreshToken save(RefreshToken refreshToken);

    Optional<RefreshToken> findByTokenHash(RefreshTokenHash tokenHash);

    Optional<RefreshToken> findById(UUID id);

    void revoke(UUID id);

    void revokeAllByUserId(UUID userId);

    void revokeByTokenHash(RefreshTokenHash tokenHash);

    boolean existsByTokenHash(RefreshTokenHash tokenHash);
}
