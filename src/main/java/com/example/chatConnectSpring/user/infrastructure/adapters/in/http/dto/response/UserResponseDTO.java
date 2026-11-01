package com.example.chatConnectSpring.user.infrastructure.adapters.in.http.dto.response;

import java.time.OffsetDateTime;
import java.util.UUID;

public record UserResponseDTO(
        UUID id,
        String email,
        String username,
        String avatarKey,
        boolean active2fa,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public UserResponseDTO(UUID id, String email, String username, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this(id, email, username, null, false, createdAt, updatedAt);
    }
}
