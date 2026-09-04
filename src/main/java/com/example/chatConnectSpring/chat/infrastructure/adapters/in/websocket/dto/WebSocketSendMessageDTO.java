package com.example.chatConnectSpring.chat.infrastructure.adapters.in.websocket.dto;

import java.util.UUID;

public record WebSocketSendMessageDTO(
        UUID chatId,
        String content
) {
}
