package com.example.chatConnectSpring.auth.infrastructure.adapters.out.redis;

import com.example.chatConnectSpring.auth.domain.ports.out.VerificationPort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Component
public class RedisVerificationAdapter implements VerificationPort {

    private final StringRedisTemplate redisTemplate;

    public RedisVerificationAdapter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void save(String key, String value, Duration ttl) {
        redisTemplate.opsForValue().set(key, value, ttl);
    }

    @Override
    public Optional<String> get(String key) {
        String value = redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(value);
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }
}
