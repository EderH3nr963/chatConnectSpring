package com.example.chatConnectSpring.chat.infrastructure.adapters.out.websocket.dto;

import com.example.chatConnectSpring.chat.infrastructure.adapters.in.http.dto.response.MessageResponseDTO;

import java.util.UUID;

public record MessageWebSocketEventDTO(
        String eventType,
        UUID chatId,
        UUID messageId,
        MessageResponseDTO message,
        UUID userId,
        String userName
) {
    public static MessageWebSocketEventDTO created(MessageResponseDTO message) {
        return new MessageWebSocketEventDTO("MESSAGE_SENT", message.chatId(), message.id(), message, null, null);
    }

    public static MessageWebSocketEventDTO updated(MessageResponseDTO message) {
        return new MessageWebSocketEventDTO("MESSAGE_EDITED", message.chatId(), message.id(), message, null, null);
    }

    public static MessageWebSocketEventDTO deleted(UUID chatId, UUID messageId) {
        return new MessageWebSocketEventDTO("MESSAGE_DELETED", chatId, messageId, null, null, null);
    }
}
