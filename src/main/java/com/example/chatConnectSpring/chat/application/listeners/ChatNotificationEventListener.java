package com.example.chatConnectSpring.chat.application.listeners;

import com.example.chatConnectSpring.chat.application.events.ChatDeletedEvent;
import com.example.chatConnectSpring.chat.application.events.ChatUpdatedEvent;
import com.example.chatConnectSpring.chat.domain.model.chat.Chat;
import com.example.chatConnectSpring.chat.domain.model.chatParticipant.ChatParticipant;
import com.example.chatConnectSpring.chat.domain.ports.out.ChatNotificationPort;
import com.example.chatConnectSpring.chat.domain.ports.out.ChatParticipantRepository;
import com.example.chatConnectSpring.chat.domain.ports.out.StoragePort;
import com.example.chatConnectSpring.chat.domain.ports.out.projections.ChatSummaryDto;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
public class ChatNotificationEventListener {

    private final ChatParticipantRepository chatParticipantRepository;
    private final ChatNotificationPort chatNotificationPort;
    private final StoragePort storagePort;

    public ChatNotificationEventListener(
            ChatParticipantRepository chatParticipantRepository,
            ChatNotificationPort chatNotificationPort,
            StoragePort storagePort) {
        this.chatParticipantRepository = chatParticipantRepository;
        this.chatNotificationPort = chatNotificationPort;
        this.storagePort = storagePort;
    }
    
    @EventListener
    @Transactional(readOnly = true)
    public void handleChatUpdated(ChatUpdatedEvent event) {
        Chat chat = event.chat();
        List<ChatParticipant> participants = chatParticipantRepository.findByChatId(chat.getId());
        
        for (ChatParticipant participant : participants) {
            ChatSummaryDto summaryDto = ChatSummaryDto.from(chat, null, participant.getUnreadMessages(), storagePort);

            chatNotificationPort.notifyChatSummaryUpdated(
                    participant.getUserId().toString(),
                    summaryDto
            );
        }
    }
    
    @EventListener
    @Transactional(readOnly = true)
    public void handleChatDeleted(ChatDeletedEvent event) {
        UUID chatId = event.chatId();
        List<ChatParticipant> participants = chatParticipantRepository.findByChatId(chatId);
        
        for (ChatParticipant participant : participants) {
            chatNotificationPort.notifyChatDeleted(
                    participant.getUserId().toString(),
                    chatId
            );
        }
    }
}