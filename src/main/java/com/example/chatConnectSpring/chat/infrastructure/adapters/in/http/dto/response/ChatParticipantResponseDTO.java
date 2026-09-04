package com.example.chatConnectSpring.chat.infrastructure.adapters.in.http.dto.response;

import com.example.chatConnectSpring.chat.domain.model.chatParticipant.ChatParticipantRole;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ChatParticipantResponseDTO(
        UUID id,
        UUID userId,
        UUID chatId,
        OffsetDateTime joinedAt,
        ChatParticipantRole role
) {
}
