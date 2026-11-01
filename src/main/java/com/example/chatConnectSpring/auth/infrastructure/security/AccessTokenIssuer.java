package com.example.chatConnectSpring.auth.infrastructure.security;

import com.example.chatConnectSpring.auth.domain.model.AccessToken;
import com.example.chatConnectSpring.auth.domain.model.vo.AccessTokenString;
import com.example.chatConnectSpring.auth.domain.ports.out.TokenIssuer;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.UUID;

@Component
public class AccessTokenIssuer implements TokenIssuer {

    private final SecretKey secretKey;
    private final long accessTokenExpiration;

    public AccessTokenIssuer(
            @Value("${jwt.secret:dev_secret_key_super_segura_para_testes_locais_123456}") String secret,
            @Value("${jwt.access-expiration:900}") long accessTokenExpiration) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiration = accessTokenExpiration;
    }

    @Override
    public AccessToken issue(UUID userId) {
        OffsetDateTime issuedAt = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime expiresAt = issuedAt.plusSeconds(accessTokenExpiration);
        String token = Jwts.builder()
                .subject(userId.toString())
                .claim("type", "access")
                .issuedAt(Date.from(issuedAt.toInstant()))
                .expiration(Date.from(expiresAt.toInstant()))
                .signWith(secretKey)
                .compact();
        return new AccessToken(token, expiresAt);
    }

    @Override
    public boolean isAccessToken(AccessTokenString token) {
        try {
            Claims claims = parseClaims(token.value());
            return isAccessType(claims) && isNotExpired(claims);
        } catch (Exception exception) {
            return false;
        }
    }

    @Override
    public String extractSubject(AccessTokenString token) {
        return parseClaims(token.value()).getSubject();
    }

    private boolean isAccessType(Claims claims) {
        return "access".equals(claims.get("type", String.class));
    }

    private boolean isNotExpired(Claims claims) {
        return claims.getExpiration().after(new Date());
    }

    private Claims parseClaims(String token) {
        return Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(token).getPayload();
    }
}
