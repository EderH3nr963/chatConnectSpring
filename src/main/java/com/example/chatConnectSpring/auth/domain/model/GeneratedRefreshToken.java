package com.example.chatConnectSpring.auth.domain.model;

import java.time.OffsetDateTime;

public record GeneratedRefreshToken(
        String value,
        OffsetDateTime expiresAt
) {
}
