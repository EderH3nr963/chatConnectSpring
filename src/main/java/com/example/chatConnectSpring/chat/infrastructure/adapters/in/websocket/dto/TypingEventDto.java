package com.example.chatConnectSpring.chat.infrastructure.adapters.in.websocket.dto;

import java.util.UUID;

public record TypingEventDto(
        UUID chatId,
        UUID userId,
        String username,
        boolean isTyping
) {
}
