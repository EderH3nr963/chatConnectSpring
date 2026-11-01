package com.example.chatConnectSpring.auth.domain.model;

import java.time.OffsetDateTime;

public record AccessToken(
        String value,
        OffsetDateTime expiresAt
) {
}