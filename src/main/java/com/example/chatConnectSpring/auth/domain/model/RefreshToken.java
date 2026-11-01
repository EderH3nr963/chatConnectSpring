package com.example.chatConnectSpring.auth.domain.model;

import com.example.chatConnectSpring.auth.domain.model.vo.RefreshTokenHash;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

public class RefreshToken {

    private final UUID id;
    private final RefreshTokenHash tokenHash;
    private final UUID userId;
    private final OffsetDateTime expiresAt;
    private OffsetDateTime revokedAt;
    private final OffsetDateTime createdAt;

    private RefreshToken(UUID id, RefreshTokenHash tokenHash, UUID userId,
                         OffsetDateTime expiresAt, OffsetDateTime revokedAt, OffsetDateTime createdAt) {
        this.id = id;
        this.tokenHash = tokenHash;
        this.userId = userId;
        this.expiresAt = expiresAt;
        this.revokedAt = revokedAt;
        this.createdAt = createdAt;
    }

    public static RefreshToken issue(UUID userId, GeneratedRefreshToken generated, RefreshTokenHash hash) {
        return new RefreshToken(
                UUID.randomUUID(),
                hash,
                userId,
                generated.expiresAt(),
                null,
                OffsetDateTime.now(ZoneOffset.UTC)
        );
    }

    public static RefreshToken reconstitute(UUID id, RefreshTokenHash tokenHash, UUID userId,
                                            OffsetDateTime expiresAt, OffsetDateTime revokedAt,
                                            OffsetDateTime createdAt) {
        return new RefreshToken(id, tokenHash, userId, expiresAt, revokedAt, createdAt);
    }

    public boolean isActive() {
        return isNotRevoked() && isNotExpired();
    }

    public void revoke() {
        ensureNotAlreadyRevoked();
        this.revokedAt = OffsetDateTime.now(ZoneOffset.UTC);
    }

    public boolean isExpired() {
        return !expiresAt.isAfter(OffsetDateTime.now(ZoneOffset.UTC));
    }

    public boolean isRevoked() {
        return revokedAt != null;
    }


    private boolean isNotRevoked() {
        return revokedAt == null;
    }

    private boolean isNotExpired() {
        return expiresAt.isAfter(OffsetDateTime.now(ZoneOffset.UTC));
    }

    private void ensureNotAlreadyRevoked() {
        if (isRevoked()) {
            throw new IllegalStateException("Token already revoked");
        }
    }

    public UUID id() { return id; }
    public RefreshTokenHash tokenHash() { return tokenHash; }
    public UUID userId() { return userId; }
    public OffsetDateTime expiresAt() { return expiresAt; }
    public OffsetDateTime revokedAt() { return revokedAt; }
    public OffsetDateTime createdAt() { return createdAt; }

}
