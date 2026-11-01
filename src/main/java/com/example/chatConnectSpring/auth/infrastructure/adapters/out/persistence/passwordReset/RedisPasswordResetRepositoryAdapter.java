package com.example.chatConnectSpring.auth.infrastructure.adapters.out.persistence.passwordReset;

import com.example.chatConnectSpring.auth.domain.model.PasswordResetRequest;
import com.example.chatConnectSpring.auth.domain.model.vo.Email;
import com.example.chatConnectSpring.auth.domain.model.vo.ResetCode;
import com.example.chatConnectSpring.auth.domain.ports.out.PasswordResetRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Optional;

@Component
public class RedisPasswordResetRepositoryAdapter implements PasswordResetRepository {

    private static final String PREFIX = "password-reset:";

    private final RedisTemplate<String, String> redisTemplate;
    private final int ttlMinutes;

    public RedisPasswordResetRepositoryAdapter(RedisTemplate<String, String> redisTemplate,
                                               @Value("${auth.password-reset.ttl-minutes:15}") int ttlMinutes) {
        this.redisTemplate = redisTemplate;
        this.ttlMinutes = ttlMinutes;
    }

    @Override
    public void save(PasswordResetRequest request) {
        String key = key(request.email());
        String value = serialize(request);
        redisTemplate.opsForValue().set(key, value, Duration.ofMinutes(ttlMinutes));
    }

    @Override
    public Optional<PasswordResetRequest> findByEmail(Email email) {
        String value = redisTemplate.opsForValue().get(key(email));
        if (value == null) return Optional.empty();
        return Optional.of(deserialize(email, value));
    }

    @Override
    public void deleteByEmail(Email email) {
        redisTemplate.delete(key(email));
    }

    private String key(Email email) {
        return PREFIX + email.value().toLowerCase();
    }

    private String serialize(PasswordResetRequest request) {
        return request.code().value() + "|" + request.expiresAt().toString() + "|" + request.attempts();
    }

    private PasswordResetRequest deserialize(Email email, String raw) {
        String[] parts = raw.split("\\|");
        ResetCode code = ResetCode.of(parts[0]);
        OffsetDateTime expiresAt = OffsetDateTime.parse(parts[1]);
        int attempts = Integer.parseInt(parts[2]);
        return PasswordResetRequest.reconstitute(email, code, expiresAt, attempts);
    }
}
