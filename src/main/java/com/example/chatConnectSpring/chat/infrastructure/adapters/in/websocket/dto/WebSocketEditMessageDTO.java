package com.example.chatConnectSpring.chat.infrastructure.adapters.in.websocket.dto;

import java.util.UUID;

public record WebSocketEditMessageDTO(
        UUID messageId,
        UUID chatId,
        String content
) {
}
