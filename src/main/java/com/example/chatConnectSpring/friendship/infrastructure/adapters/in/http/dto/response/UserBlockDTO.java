package com.example.chatConnectSpring.friendship.infrastructure.adapters.in.http.dto.response;

import java.time.OffsetDateTime;
import java.util.UUID;

public record UserBlockDTO(
    UUID blockerId,
    String blockerUsername,
    OffsetDateTime createdAt
) {
}
