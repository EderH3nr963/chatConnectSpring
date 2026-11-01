package com.example.chatConnectSpring.auth.infrastructure.adapters.out.persistence.twoFactor;

import com.example.chatConnectSpring.auth.domain.model.TwoFactorVerification;
import com.example.chatConnectSpring.auth.domain.model.vo.TwoFactorCode;
import com.example.chatConnectSpring.auth.domain.ports.out.TwoFactorVerificationRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
public class RedisTwoFactorVerificationAdapter implements TwoFactorVerificationRepository {

    private final StringRedisTemplate redisTemplate;

    public RedisTwoFactorVerificationAdapter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void save(TwoFactorVerification verification) {
        String value = verification.code().value() + "|" + verification.expiresAt().toString();
        redisTemplate.opsForValue().set(verification.redisKey(), value, verification.ttl());
    }

    @Override
    public Optional<TwoFactorVerification> findByUserId(UUID userId) {
        String key = "2fa:" + userId;
        String raw = redisTemplate.opsForValue().get(key);
        if (raw == null) {
            return Optional.empty();
        }
        String[] parts = raw.split("\\|", 2);
        TwoFactorCode code = TwoFactorCode.of(parts[0]);
        Instant expiresAt = Instant.parse(parts[1]);
        return Optional.of(TwoFactorVerification.reconstitute(userId, code, expiresAt));
    }

    @Override
    public void deleteByUserId(UUID userId) {
        redisTemplate.delete("2fa:" + userId);
    }
}
