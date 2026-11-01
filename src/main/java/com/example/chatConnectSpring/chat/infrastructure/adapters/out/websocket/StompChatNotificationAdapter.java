package com.example.chatConnectSpring.chat.infrastructure.adapters.out.websocket;

import com.example.chatConnectSpring.chat.domain.model.chat.Chat;
import com.example.chatConnectSpring.chat.domain.ports.out.ChatNotificationPort;
import com.example.chatConnectSpring.chat.domain.ports.out.projections.ChatSummaryDto;
import com.example.chatConnectSpring.chat.infrastructure.adapters.in.websocket.dto.TypingEventDto;
import com.example.chatConnectSpring.chat.infrastructure.adapters.out.websocket.dto.ChatWebSocketEventDTO;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class StompChatNotificationAdapter implements ChatNotificationPort {
    
    private static final String USER_NOTIFICATIONS_QUEUE = "/queue/chat/notifications";
    
    private final SimpMessagingTemplate messagingTemplate;
    
    public StompChatNotificationAdapter(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }
    
    @Override
    public void notifyJoinedChat(UUID userId, Chat chat) {
        if (userId == null || chat == null || chat.getId() == null) return;
        
        ChatWebSocketEventDTO event = ChatWebSocketEventDTO.joined(chat);
        messagingTemplate.convertAndSendToUser(userId.toString(), USER_NOTIFICATIONS_QUEUE, event);
    }
    
    @Override
    public void notifyLeftChat(UUID userId, UUID chatId) {
        if (userId == null || chatId == null) return;
        
        ChatWebSocketEventDTO event = ChatWebSocketEventDTO.leaved(chatId);
        messagingTemplate.convertAndSendToUser(userId.toString(), USER_NOTIFICATIONS_QUEUE, event);
    }
    
    @Override
    public void notifyChatSummaryUpdated(String participantUserId, ChatSummaryDto summaryEvent) {
        if (participantUserId == null || summaryEvent == null) return;
        
        ChatWebSocketEventDTO event = ChatWebSocketEventDTO.updated(summaryEvent);
        messagingTemplate.convertAndSendToUser(participantUserId, USER_NOTIFICATIONS_QUEUE, event);
    }
    
    @Override
    public void notifyNewMessage(String participantUserId, ChatSummaryDto summaryEvent) {
        if (participantUserId == null || summaryEvent == null) return;
        
        ChatWebSocketEventDTO event = ChatWebSocketEventDTO.notification(summaryEvent);
        messagingTemplate.convertAndSendToUser(participantUserId, USER_NOTIFICATIONS_QUEUE, event);
    }
    
    @Override
    public void notifyChatDeleted(String participantUserId, UUID chatId) {
        if (participantUserId == null || chatId == null) return;
        
        ChatWebSocketEventDTO event = ChatWebSocketEventDTO.deleted(chatId);
        messagingTemplate.convertAndSendToUser(participantUserId, USER_NOTIFICATIONS_QUEUE, event);
    }
    
    @Override
    public void notifyTypingStatus(TypingEventDto typingEvent) {
        if (typingEvent == null || typingEvent.chatId() == null) return;
        
        String destination = "/topic/chat." + typingEvent.chatId() + "/typing";
        messagingTemplate.convertAndSend(destination, typingEvent);
    }
}