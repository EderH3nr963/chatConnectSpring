package com.example.chatConnectSpring.user.infrastructure.adapters.in.http.dto.response;

import java.time.OffsetDateTime;
import java.util.UUID;

public record UserResponseDTO(
        UUID id,
        String clerkUserId,
        String email,
        String username,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}
