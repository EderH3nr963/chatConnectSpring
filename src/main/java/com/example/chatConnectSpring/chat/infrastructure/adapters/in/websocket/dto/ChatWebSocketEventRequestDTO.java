package com.example.chatConnectSpring.chat.infrastructure.adapters.in.websocket.dto;

import com.example.chatConnectSpring.chat.infrastructure.adapters.in.websocket.ChatWebSocketEventType;

import java.util.UUID;

public record ChatWebSocketEventRequestDTO(
        ChatWebSocketEventType event,
        UUID chatId,
        UUID messageId,
        UUID userId,
        String content
) {}
