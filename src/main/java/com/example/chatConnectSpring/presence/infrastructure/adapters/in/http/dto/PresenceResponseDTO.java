package com.example.chatConnectSpring.presence.infrastructure.adapters.in.http.dto;

import com.example.chatConnectSpring.presence.domain.model.Presence;

import java.time.Instant;
import java.util.UUID;

public record PresenceResponseDTO(
        UUID userId,
        String status,
        Instant lastSeen,
        Instant onlineSince
) {
    public static PresenceResponseDTO from(Presence presence) {
        return new PresenceResponseDTO(
                presence.userId(),
                presence.status().name(),
                presence.lastSeen(),
                presence.onlineSince()
        );
    }
}
