package com.example.chatConnectSpring.chat.infrastructure.adapters.in.http.dto.response;

import com.example.chatConnectSpring.chat.domain.model.chat.Chat;
import com.example.chatConnectSpring.chat.domain.model.chat.ChatTypeEnum;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record ChatResponseDTO(
        UUID id,
        String title,
        String description,
        ChatTypeEnum chatType,
        List<ChatParticipantResponseDTO> participants,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public static ChatResponseDTO fromChat(Chat chat) {
        return new ChatResponseDTO(
                chat.getId(),
                chat.getTitle(),
                chat.getDescription(),
                chat.getChatType(),
                null,
                chat.getCreatedAt(),
                chat.getUpdatedAt()
        );
    }
}
