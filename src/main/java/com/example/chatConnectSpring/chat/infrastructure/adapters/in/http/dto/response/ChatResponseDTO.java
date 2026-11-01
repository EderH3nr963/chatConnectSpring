package com.example.chatConnectSpring.chat.infrastructure.adapters.in.http.dto.response;

import com.example.chatConnectSpring.chat.domain.model.chat.Chat;
import com.example.chatConnectSpring.chat.domain.model.chat.ChatTypeEnum;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record ChatResponseDTO(
        UUID id,
        String name,
        String avatarUrl,
        String description,
        ChatTypeEnum chatType,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public static ChatResponseDTO fromChat(Chat chat) {
        return new ChatResponseDTO(
                chat.getId(),
                chat.getName(),
                chat.getAvatarKey(),
                chat.getDescription(),
                chat.getChatType(),
                chat.getCreatedAt(),
                chat.getUpdatedAt()
        );
    }
}
