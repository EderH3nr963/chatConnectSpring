package com.example.chatConnectSpring.chat.infrastructure.adapters.in.websocket;

import java.util.UUID;

public record ChatWsMessage(
        ChatWsMessageType type,
        UUID userId,
        UUID chatId,
        String message
) {
}
