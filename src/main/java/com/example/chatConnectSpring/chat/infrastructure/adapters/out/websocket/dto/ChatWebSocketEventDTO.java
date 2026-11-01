package com.example.chatConnectSpring.chat.infrastructure.adapters.out.websocket.dto;

import com.example.chatConnectSpring.chat.domain.model.chat.Chat;
import com.example.chatConnectSpring.chat.domain.ports.out.projections.ChatSummaryDto;

import java.util.UUID;

public record ChatWebSocketEventDTO(
        String eventType,
        Object data
) {
    public static ChatWebSocketEventDTO joined(Chat chat) {
        return new ChatWebSocketEventDTO("JOINED_CHAT", chat);
    }
    
    public static ChatWebSocketEventDTO leaved(UUID chatId) {
        return new ChatWebSocketEventDTO("LEAVED_CHAT", chatId);
    }
    
    public static ChatWebSocketEventDTO updated(ChatSummaryDto chatSummaryDto) {
        return new ChatWebSocketEventDTO("UPDATED_CHAT", chatSummaryDto);
    }
    
    public static ChatWebSocketEventDTO notification(ChatSummaryDto chatSummaryDto) {
        return new ChatWebSocketEventDTO("NEW_MESSAGE_NOTIFICATION", chatSummaryDto);
    }
    
    public static ChatWebSocketEventDTO deleted(UUID chatId) {
        return new ChatWebSocketEventDTO("DELETED_CHAT", chatId);
    }
}
