package com.example.chatConnectSpring.chat.infrastructure.mapper;

import com.example.chatConnectSpring.chat.domain.model.chat.Chat;
import com.example.chatConnectSpring.chat.domain.model.chatParticipant.ChatParticipant;
import com.example.chatConnectSpring.chat.infrastructure.adapters.out.persistence.chat.ChatEntity;
import com.example.chatConnectSpring.chat.infrastructure.adapters.in.http.dto.response.ChatParticipantResponseDTO;
import com.example.chatConnectSpring.chat.infrastructure.adapters.in.http.dto.response.ChatResponseDTO;

import java.util.List;

public class ChatMapper {
    public static ChatEntity toEntity(Chat chat) {
        if (chat == null) {
            return null;
        }

        ChatEntity entity = new ChatEntity();
        entity.setId(chat.getId());
        entity.setTitle(chat.getTitle());
        entity.setDescription(chat.getDescription());
        entity.setChatType(chat.getChatType());
        entity.setCreatedAt(chat.getCreatedAt());
        entity.setUpdatedAt(chat.getUpdatedAt());
        return entity;
    }

    public static Chat toDomain(ChatEntity entity) {
        if (entity == null) {
            return null;
        }

        Chat domain = new Chat();
        domain.setId(entity.getId());
        domain.setTitle(entity.getTitle());
        domain.setDescription(entity.getDescription());
        domain.setChatType(entity.getChatType());
        domain.setCreatedAt(entity.getCreatedAt());
        domain.setUpdatedAt(entity.getUpdatedAt());
        return domain;
    }

    public static ChatResponseDTO toDTO(Chat chat) {
        if (chat == null) {
            return null;
        }

        List<ChatParticipantResponseDTO> participants = chat.getParticipants() == null
                ? null
                : chat.getParticipants()
                .stream()
                .map(ChatMapper::toDTO)
                .toList();

        return new ChatResponseDTO(
                chat.getId(),
                chat.getTitle(),
                chat.getDescription(),
                chat.getChatType(),
                participants,
                chat.getCreatedAt(),
                chat.getUpdatedAt()
        );
    }

    private static ChatParticipantResponseDTO toDTO(ChatParticipant participant) {
        if (participant == null) {
            return null;
        }

        return new ChatParticipantResponseDTO(
                participant.getId(),
                participant.getUserId(),
                participant.getChatId(),
                participant.getJoinedAt(),
                participant.getRole()
        );
    }
}
