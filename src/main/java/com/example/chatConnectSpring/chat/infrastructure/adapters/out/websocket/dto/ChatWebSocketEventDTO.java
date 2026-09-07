package com.example.chatConnectSpring.chat.infrastructure.adapters.out.websocket.dto;

import com.example.chatConnectSpring.chat.domain.model.chat.Chat;
import com.example.chatConnectSpring.chat.infrastructure.adapters.in.http.dto.response.ChatResponseDTO;

import java.util.UUID;

public record ChatWebSocketEventDTO(
        String eventType,
        UUID userId,
        UUID chatId,
        ChatResponseDTO chat
) {
    public static ChatWebSocketEventDTO joined(UUID userId, Chat chat) {
        return new ChatWebSocketEventDTO("JOINED_CHAT", userId, chat.getId(), ChatResponseDTO.fromChat(chat));
    }
    
    public static ChatWebSocketEventDTO leaved(UUID userId, UUID chatId) {
        return new ChatWebSocketEventDTO("LEAVED_CHAT", userId, chatId, null);
    }
}
