package com.example.chatConnectSpring.auth.infrastructure.security;

import com.example.chatConnectSpring.auth.domain.model.GeneratedRefreshToken;
import com.example.chatConnectSpring.auth.domain.model.vo.RawRefreshToken;
import com.example.chatConnectSpring.auth.domain.model.vo.RefreshTokenHash;
import com.example.chatConnectSpring.auth.domain.ports.out.RefreshTokenIssuer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.HexFormat;

@Component
public class RefreshTokenIssuerImpl implements RefreshTokenIssuer {

    private final SecureRandom secureRandom = new SecureRandom();
    private final long expirationSeconds;

    public RefreshTokenIssuerImpl(@Value("${jwt.refresh-expiration:2592000}") long expirationSeconds) {
        this.expirationSeconds = expirationSeconds;
    }

    @Override
    public GeneratedRefreshToken issue() {
        byte[] randomBytes = new byte[64];
        secureRandom.nextBytes(randomBytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        OffsetDateTime expiresAt = OffsetDateTime.now(ZoneOffset.UTC).plusSeconds(expirationSeconds);
        return new GeneratedRefreshToken(token, expiresAt);
    }

    @Override
    public RefreshTokenHash hash(RawRefreshToken raw) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(raw.value().getBytes(StandardCharsets.UTF_8));
            return RefreshTokenHash.of(HexFormat.of().formatHex(digest));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }
}
