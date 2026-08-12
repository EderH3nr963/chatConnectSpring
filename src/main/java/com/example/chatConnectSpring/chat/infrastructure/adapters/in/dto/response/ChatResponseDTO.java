package com.example.chatConnectSpring.chat.infrastructure.adapters.in.dto.response;

import com.example.chatConnectSpring.chat.infrastructure.adapters.out.persistence.chat.ChatTypeEnum;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record ChatResponseDTO(
        UUID id,
        ChatTypeEnum type,
        String title,
        UUID createdByUserId,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        boolean active,
        List<ChatParticipantResponseDTO> participants
) {}
