package com.example.chatConnectSpring.chat.infrastructure.adapters.out.websocket;

import com.example.chatConnectSpring.chat.domain.model.chat.Chat;
import com.example.chatConnectSpring.chat.domain.ports.out.ChatNotificationPort;
import com.example.chatConnectSpring.chat.infrastructure.adapters.out.websocket.dto.ChatWebSocketEventDTO;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.UUID;

public class StompChatNotificationAdapter implements ChatNotificationPort {
    private final SimpMessagingTemplate messagingTemplate;
    
    public StompChatNotificationAdapter(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }
    
    @Override
    public void notifyJoinedChat(UUID userId, Chat chat) {
        if (chat == null || chat.getId() == null || userId == null)
            return;
        
        ChatWebSocketEventDTO event = ChatWebSocketEventDTO.joined(userId, chat);
        
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/notifications",
                event
        );
    }
    
    @Override
    public void notifyLeftChat(UUID userId, UUID chatId) {
        if (chatId == null || userId == null)
            return;
        
        ChatWebSocketEventDTO event = ChatWebSocketEventDTO.leaved(userId, chatId);
        
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/notifications",
                event
        );
    }
}
