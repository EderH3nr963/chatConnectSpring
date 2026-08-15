package com.example.chatConnectSpring.chat.infrastructure.adapters.in.websocket.dto;

import com.example.chatConnectSpring.chat.infrastructure.adapters.in.websocket.ChatWebSocketEventType;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ChatWebSocketEventResponseDTO(
        ChatWebSocketEventType event,
        UUID chatId,
        UUID messageId,
        UUID userId,
        String content,
        OffsetDateTime timestamp
) {}
