package com.example.chatConnectSpring.auth.domain.model;

import com.example.chatConnectSpring.auth.domain.model.vo.Email;
import com.example.chatConnectSpring.auth.domain.model.vo.ResetCode;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class PasswordResetRequest {

    private final Email email;
    private final ResetCode code;
    private final OffsetDateTime expiresAt;
    private int attempts;

    private static final int MAX_ATTEMPTS = 5;

    private PasswordResetRequest(Email email, ResetCode code, OffsetDateTime expiresAt, int attempts) {
        this.email = email;
        this.code = code;
        this.expiresAt = expiresAt;
        this.attempts = attempts;
    }

    public static PasswordResetRequest create(Email email, int ttlMinutes) {
        return new PasswordResetRequest(
                email,
                ResetCode.generate(),
                OffsetDateTime.now(ZoneOffset.UTC).plusMinutes(ttlMinutes),
                0
        );
    }

    public static PasswordResetRequest reconstitute(Email email, ResetCode code, OffsetDateTime expiresAt, int attempts) {
        return new PasswordResetRequest(email, code, expiresAt, attempts);
    }

    public boolean isExpired() {
        return OffsetDateTime.now(ZoneOffset.UTC).isAfter(expiresAt);
    }

    public boolean hasAttemptsLeft() {
        return attempts < MAX_ATTEMPTS;
    }

    public void incrementAttempts() {
        attempts++;
    }

    public Email email() { return email; }
    public ResetCode code() { return code; }
    public OffsetDateTime expiresAt() { return expiresAt; }
    public int attempts() { return attempts; }
}
