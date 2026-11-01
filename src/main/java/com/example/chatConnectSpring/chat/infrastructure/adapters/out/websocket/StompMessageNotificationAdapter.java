package com.example.chatConnectSpring.chat.infrastructure.adapters.out.websocket;

import com.example.chatConnectSpring.chat.domain.model.message.Message;
import com.example.chatConnectSpring.chat.domain.ports.out.MessageNotificationPort;
import com.example.chatConnectSpring.chat.domain.ports.out.StoragePort;
import com.example.chatConnectSpring.chat.infrastructure.adapters.in.http.dto.response.MessageResponseDTO;
import com.example.chatConnectSpring.chat.infrastructure.adapters.out.websocket.dto.MessageWebSocketEventDTO;
import com.example.chatConnectSpring.chat.infrastructure.mapper.MessageMapper;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class StompMessageNotificationAdapter implements MessageNotificationPort {

    private final SimpMessagingTemplate messagingTemplate;
    private final StoragePort storagePort;

    public StompMessageNotificationAdapter(SimpMessagingTemplate messagingTemplate, StoragePort storagePort) {
        this.messagingTemplate = messagingTemplate;
        this.storagePort = storagePort;
    }

    @Override
    public void notifyMessageSent(Message message) {
        if (message == null || message.getChatId() == null) return;
        MessageResponseDTO dto = MessageMapper.toDTO(message, storagePort);
        MessageWebSocketEventDTO event = MessageWebSocketEventDTO.created(dto);
        String chatDestination = "/topic/chat." + message.getChatId();
        messagingTemplate.convertAndSend(chatDestination, event);
    }

    @Override
    public void notifyMessageEdited(Message message) {
        if (message == null || message.getChatId() == null) return;
        MessageResponseDTO dto = MessageMapper.toDTO(message, storagePort);
        MessageWebSocketEventDTO event = MessageWebSocketEventDTO.updated(dto);
        String destination = "/topic/chat." + message.getChatId();
        messagingTemplate.convertAndSend(destination, event);
    }

    @Override
    public void notifyMessageDeleted(UUID chatId, UUID messageId) {
        if (chatId == null || messageId == null) return;
        MessageWebSocketEventDTO event = MessageWebSocketEventDTO.deleted(chatId, messageId);
        String destination = "/topic/chat." + chatId;
        messagingTemplate.convertAndSend(destination, event);
    }
}
