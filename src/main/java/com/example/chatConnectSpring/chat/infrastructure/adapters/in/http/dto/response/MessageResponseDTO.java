package com.example.chatConnectSpring.chat.infrastructure.adapters.in.http.dto.response;

import java.time.OffsetDateTime;
import java.util.UUID;

public record MessageResponseDTO(
        UUID id,
        UUID chatId,
        UUID senderId,
        String content,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
