package com.example.chatConnectSpring.presence.domain.ports.out;

import com.example.chatConnectSpring.presence.domain.model.Presence;

import java.util.Optional;
import java.util.UUID;

public interface PresenceRepository {

    void save(Presence presence);

    Optional<Presence> findByUserId(UUID userId);

    boolean isOnline(UUID userId);

    void remove(UUID userId);

    java.util.Set<UUID> filterOnline(java.util.Collection<UUID> userIds);
}
