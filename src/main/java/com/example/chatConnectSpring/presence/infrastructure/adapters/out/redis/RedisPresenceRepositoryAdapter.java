package com.example.chatConnectSpring.presence.infrastructure.adapters.out.redis;

import com.example.chatConnectSpring.presence.domain.model.Presence;
import com.example.chatConnectSpring.presence.domain.model.PresenceStatus;
import com.example.chatConnectSpring.presence.domain.ports.out.PresenceRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
public class RedisPresenceRepositoryAdapter implements PresenceRepository {

    private static final String KEY_PREFIX = "presence:user:";
    private static final String ONLINE_SET = "presence:online";
    private static final Duration TTL = Duration.ofSeconds(90);

    private final StringRedisTemplate redis;

    public RedisPresenceRepositoryAdapter(StringRedisTemplate redis) {
        this.redis = redis;
    }

    @Override
    public void save(Presence presence) {
        String key = KEY_PREFIX + presence.userId();
        String value = serialize(presence);
        redis.opsForValue().set(key, value, TTL);
        updateOnlineSet(presence);
    }

    @Override
    public Optional<Presence> findByUserId(UUID userId) {
        String value = redis.opsForValue().get(KEY_PREFIX + userId);
        if (value == null) return Optional.empty();
        return Optional.of(deserialize(userId, value));
    }

    @Override
    public boolean isOnline(UUID userId) {
        Boolean hasKey = redis.hasKey(KEY_PREFIX + userId);
        if (!Boolean.TRUE.equals(hasKey)) return false;
        return findByUserId(userId).map(Presence::isOnline).orElse(false);
    }

    @Override
    public void remove(UUID userId) {
        redis.delete(KEY_PREFIX + userId);
        redis.opsForSet().remove(ONLINE_SET, userId.toString());
    }

    @Override
    public java.util.Set<UUID> filterOnline(java.util.Collection<UUID> userIds) {
        if (userIds == null || userIds.isEmpty()) return java.util.Set.of();
        java.util.Set<UUID> online = new java.util.HashSet<>();
        for (UUID id : userIds) {
            if (Boolean.TRUE.equals(redis.hasKey(KEY_PREFIX + id))) {
                findByUserId(id).filter(Presence::isOnline).ifPresent(p -> online.add(id));
            }
        }
        return java.util.Set.copyOf(online);
    }

    private String serialize(Presence presence) {
        String onlineSince = presence.onlineSince() != null
                ? String.valueOf(presence.onlineSince().toEpochMilli())
                : "";
        return presence.status().name() + "|" + presence.lastSeen().toEpochMilli() + "|" + onlineSince;
    }

    private Presence deserialize(UUID userId, String value) {
        String[] parts = value.split("\\|", -1);
        PresenceStatus status = PresenceStatus.valueOf(parts[0]);
        Instant lastSeen = Instant.ofEpochMilli(Long.parseLong(parts[1]));
        Instant onlineSince = parts.length > 2 && !parts[2].isBlank()
                ? Instant.ofEpochMilli(Long.parseLong(parts[2]))
                : null;
        return Presence.of(userId, status, lastSeen, onlineSince);
    }

    private void updateOnlineSet(Presence presence) {
        String member = presence.userId().toString();
        if (presence.isOnline()) {
            redis.opsForSet().add(ONLINE_SET, member);
        } else {
            redis.opsForSet().remove(ONLINE_SET, member);
        }
    }
}
