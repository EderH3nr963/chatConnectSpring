package com.example.chatConnectSpring.auth.domain.model;

import com.example.chatConnectSpring.auth.domain.model.vo.TwoFactorCode;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

/**
 * OC #1 One Level of Indentation — Tell Don't Ask.
 * Agrega código + expiração; a entidade decide se expirou.
 */
public final class TwoFactorVerification {

    private static final Duration TTL = Duration.ofMinutes(10);

    private final UUID userId;
    private final TwoFactorCode code;
    private final Instant expiresAt;

    private TwoFactorVerification(UUID userId, TwoFactorCode code, Instant expiresAt) {
        this.userId = userId;
        this.code = code;
        this.expiresAt = expiresAt;
    }

    public static TwoFactorVerification create(UUID userId) {
        return new TwoFactorVerification(userId, TwoFactorCode.generate(), Instant.now().plus(TTL));
    }

    public static TwoFactorVerification reconstitute(UUID userId, TwoFactorCode code, Instant expiresAt) {
        return new TwoFactorVerification(userId, code, expiresAt);
    }

    public boolean isExpired(Instant now) {
        return now.isAfter(expiresAt);
    }

    public boolean codeMatches(TwoFactorCode candidate) {
        return code.matches(candidate);
    }

    public UUID userId() {
        return userId;
    }

    public TwoFactorCode code() {
        return code;
    }

    public Instant expiresAt() {
        return expiresAt;
    }

    public Duration ttl() {
        return TTL;
    }

    public String redisKey() {
        return "2fa:" + userId;
    }
}
