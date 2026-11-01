package com.example.chatConnectSpring.presence.domain.ports.in;

import com.example.chatConnectSpring.presence.domain.model.Presence;

import java.util.UUID;

public interface PresenceUseCase {

    void markOnline(UUID userId);

    void markOffline(UUID userId);

    void heartbeat(UUID userId);

    Presence get(UUID userId);

    boolean isOnline(UUID userId);
}
