package com.example.chatConnectSpring.chat.application.mapper;

import com.example.chatConnectSpring.chat.domain.model.Chat;
import com.example.chatConnectSpring.chat.domain.model.ChatParticipant;
import com.example.chatConnectSpring.chat.infrastructure.adapters.in.dto.response.ChatParticipantResponseDTO;
import com.example.chatConnectSpring.chat.infrastructure.adapters.in.dto.response.ChatResponseDTO;

import java.util.List;

public class ChatResponseMapper {

    public static ChatResponseDTO toDTO(Chat chat) {
        return new ChatResponseDTO(
                chat.getId(),
                chat.getType(),
                chat.getTitle(),
                chat.getCreatedByUserId(),
                chat.getCreatedAt(),
                chat.getUpdatedAt(),
                chat.isActive(),
                null
        );
    }

    public static ChatParticipantResponseDTO toDTO(ChatParticipant participant) {
        return new ChatParticipantResponseDTO(
                participant.getId(),
                participant.getChatId(),
                participant.getUserId(),
                participant.getJoinedAt(),
                participant.getLastReadAt(),
                participant.isMuted(),
                participant.isBlocked()
        );
    }

    public static ChatResponseDTO toDTO(Chat chat, List<ChatParticipant> participants) {
        return new ChatResponseDTO(
                chat.getId(),
                chat.getType(),
                chat.getTitle(),
                chat.getCreatedByUserId(),
                chat.getCreatedAt(),
                chat.getUpdatedAt(),
                chat.isActive(),
                participants == null ? null : participants.stream().map(ChatResponseMapper::toDTO).toList()
        );
    }
}
