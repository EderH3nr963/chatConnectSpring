package com.example.chatConnectSpring.chat.infrastructure.adapters.out.websocket;

import com.example.chatConnectSpring.chat.domain.model.Message;
import com.example.chatConnectSpring.chat.domain.model.chatParticipant.ChatParticipant;
import com.example.chatConnectSpring.chat.domain.ports.out.MessageNotificationPort;
import com.example.chatConnectSpring.chat.infrastructure.adapters.in.http.dto.response.MessageResponseDTO;
import com.example.chatConnectSpring.chat.infrastructure.adapters.out.websocket.dto.MessageWebSocketEventDTO;
import com.example.chatConnectSpring.chat.infrastructure.mapper.MessageMapper;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class StompMessageNotificationAdapter implements MessageNotificationPort {

    private final SimpMessagingTemplate messagingTemplate;

    public StompMessageNotificationAdapter(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }
    
    @Override
    public void notifyMessageSent(Message message, List<ChatParticipant> participantsNonActiveInChat) {
        if (message == null || message.getChatId() == null) 
            return;
        
        MessageResponseDTO dto = MessageMapper.toDTO(message);
        MessageWebSocketEventDTO event = MessageWebSocketEventDTO.created(dto);
        
        String chatDestination = "/topic/chat." + message.getChatId();
        messagingTemplate.convertAndSend(chatDestination, event);
        
        if (participantsNonActiveInChat == null)
            return;
        
        participantsNonActiveInChat.forEach(participant -> {
            if (participant != null && participant.getUserId() != null) {
                
                messagingTemplate.convertAndSendToUser(
                        participant.getUserId().toString(),
                        "/queue/notifications",
                        event
                );
            }
        });
    }

    @Override
    public void notifyMessageEdited(Message message) {
        if (message == null || message.getChatId() == null) {
            return;
        }
        MessageResponseDTO dto = MessageMapper.toDTO(message);
        MessageWebSocketEventDTO event = MessageWebSocketEventDTO.updated(dto);
        String destination = "/topic/chat." + message.getChatId();
        messagingTemplate.convertAndSend(destination, event);
    }

    @Override
    public void notifyMessageDeleted(UUID chatId, UUID messageId) {
        if (chatId == null || messageId == null) {
            return;
        }
        MessageWebSocketEventDTO event = MessageWebSocketEventDTO.deleted(chatId, messageId);
        String destination = "/topic/chat." + chatId;
        messagingTemplate.convertAndSend(destination, event);
    }
    
}
