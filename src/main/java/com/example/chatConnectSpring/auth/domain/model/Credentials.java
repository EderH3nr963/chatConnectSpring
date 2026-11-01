package com.example.chatConnectSpring.auth.domain.model;

import com.example.chatConnectSpring.auth.domain.model.vo.Email;
import com.example.chatConnectSpring.auth.domain.model.vo.PasswordHash;
import com.example.chatConnectSpring.auth.domain.model.vo.RawPassword;
import com.example.chatConnectSpring.auth.domain.model.vo.PasswordHasher;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

public class Credentials {

    private final UUID id;
    private final UUID userId;
    private final Email email;
    private final PasswordHash passwordHash;
    private final OffsetDateTime loggedAt;

    private Credentials(UUID id, UUID userId, Email email, PasswordHash passwordHash, OffsetDateTime loggedAt) {
        this.id = id;
        this.userId = userId;
        this.email = email;
        this.passwordHash = passwordHash;
        this.loggedAt = loggedAt;
    }

    public static Credentials create(UUID userId, Email email, RawPassword rawPassword, PasswordHasher hasher) {
        PasswordHash hash = hasher.hash(rawPassword);
        return new Credentials(UUID.randomUUID(), userId, email, hash, OffsetDateTime.now(ZoneOffset.UTC));
    }

    public static Credentials reconstitute(UUID id, UUID userId, Email email, PasswordHash hash, OffsetDateTime loggedAt) {
        return new Credentials(id, userId, email, hash, loggedAt);
    }

    public boolean matches(RawPassword raw, PasswordHasher hasher) {
        return passwordHash.matches(raw, hasher);
    }

    public UUID id() { return id; }
    public UUID userId() { return userId; }
    public Email email() { return email; }
    public PasswordHash passwordHash() { return passwordHash; }
    public OffsetDateTime loggedAt() { return loggedAt; }
}
