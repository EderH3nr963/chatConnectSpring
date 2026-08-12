package com.example.chatConnectSpring.chat.infrastructure.adapters.in.dto.response;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ChatParticipantResponseDTO(
        UUID id,
        UUID chatId,
        UUID userId,
        OffsetDateTime joinedAt,
        OffsetDateTime lastReadAt,
        boolean muted,
        boolean blocked
) {}
