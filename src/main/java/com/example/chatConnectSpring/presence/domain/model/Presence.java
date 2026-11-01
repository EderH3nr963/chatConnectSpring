package com.example.chatConnectSpring.presence.domain.model;

import java.time.Instant;
import java.util.UUID;

public final class Presence {

    private final UUID userId;
    private final PresenceStatus status;
    private final Instant lastSeen;
    private final Instant onlineSince;

    private Presence(UUID userId, PresenceStatus status, Instant lastSeen, Instant onlineSince) {
        this.userId = userId;
        this.status = status;
        this.lastSeen = lastSeen;
        this.onlineSince = onlineSince;
    }

    public static Presence online(UUID userId) {
        Instant now = Instant.now();
        return new Presence(userId, PresenceStatus.ONLINE, now, now);
    }

    public static Presence offline(UUID userId) {
        return new Presence(userId, PresenceStatus.OFFLINE, Instant.now(), null);
    }

    public static Presence of(UUID userId, PresenceStatus status, Instant lastSeen, Instant onlineSince) {
        return new Presence(userId, status, lastSeen, onlineSince);
    }

    public Presence heartbeat() {
        return new Presence(userId, PresenceStatus.ONLINE, Instant.now(), onlineSince);
    }

    public boolean isOnline() {
        return status == PresenceStatus.ONLINE;
    }

    public UUID userId() { return userId; }
    public PresenceStatus status() { return status; }
    public Instant lastSeen() { return lastSeen; }
    public Instant onlineSince() { return onlineSince; }
}
