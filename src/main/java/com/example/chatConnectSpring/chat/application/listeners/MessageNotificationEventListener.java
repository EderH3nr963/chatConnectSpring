package com.example.chatConnectSpring.chat.application.listeners;

import com.example.chatConnectSpring.chat.application.events.MessageSentEvent;
import com.example.chatConnectSpring.chat.domain.model.chat.Chat;
import com.example.chatConnectSpring.chat.domain.model.chat.ChatTypeEnum;
import com.example.chatConnectSpring.chat.domain.model.chatParticipant.ChatParticipant;
import com.example.chatConnectSpring.chat.domain.model.message.Message;
import com.example.chatConnectSpring.chat.domain.ports.out.*;
import com.example.chatConnectSpring.chat.domain.ports.out.projections.ChatSummaryDto;
import com.example.chatConnectSpring.user.domain.port.out.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

@Component
public class MessageNotificationEventListener {

    private static final Logger log = LoggerFactory.getLogger(MessageNotificationEventListener.class);
    
    private final PresenceStateOutputPort presenceStateOutputPort;
    private final ChatParticipantRepository chatParticipantRepository;
    private final ChatRepository chatRepository;
    private final ChatNotificationPort chatNotificationPort;
    private final MessageNotificationPort messageNotificationPort;
    private final MessageRepository messageRepository;
    private final UsuarioRepository usuarioRepository;
    private final StoragePort storagePort;

    public MessageNotificationEventListener(
            ChatNotificationPort chatNotificationPort,
            PresenceStateOutputPort presenceStateOutputPort,
            ChatRepository chatRepository,
            ChatParticipantRepository chatParticipantRepository,
            MessageNotificationPort messageNotificationPort,
            MessageRepository messageRepository,
            UsuarioRepository usuarioRepository,
            StoragePort storagePort
    ) {
        this.presenceStateOutputPort = presenceStateOutputPort;
        this.chatNotificationPort = chatNotificationPort;
        this.chatRepository = chatRepository;
        this.chatParticipantRepository = chatParticipantRepository;
        this.messageNotificationPort = messageNotificationPort;
        this.messageRepository = messageRepository;
        this.usuarioRepository = usuarioRepository;
        this.storagePort = storagePort;
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMessageSent(MessageSentEvent event) {
        Message message = messageRepository.findById(event.messageId());
        if (message == null) return;
        Chat chat = chatRepository.findById(message.getChatId());
        if (chat == null) return;
        
        if (message == null || message.getChat() == null || message.getSender() == null) {
            log.error("Failed to load fully populated message or relationships for id={}", event.messageId());
            return;
        }
        
        List<ChatParticipant> participants = chatParticipantRepository.findByChatId(message.getChatId());
        Set<String> activeUsers = presenceStateOutputPort.usersInChat(message.getChatId().toString());
        
        participants.forEach(participant -> processParticipantNotification(chat, message, participant, participants, activeUsers));
        
        chatParticipantRepository.saveAll(participants);
        log.info("Message notification sent: messageId={} chatId={}", message.getId(), chat.getId());
        messageNotificationPort.notifyMessageSent(message);
    }
    
    private void processParticipantNotification(
            Chat chat,
            Message message,
            ChatParticipant currentParticipant,
            List<ChatParticipant> allParticipants,
            Set<String> activeUsers) {
        
        updateUnreadCountIfInactive(currentParticipant, activeUsers);
        updateLastReadMessageIfActive(currentParticipant, activeUsers);
        
        ChatSummaryDto summaryDto = buildSummaryDto(chat, message, currentParticipant, allParticipants);
        
        chatNotificationPort.notifyChatSummaryUpdated(currentParticipant.getUserId().toString(), summaryDto);
        
        if (!currentParticipant.isMuted()) {
            chatNotificationPort.notifyNewMessage(currentParticipant.getUserId().toString(), summaryDto);
        }
    }
    
    private void updateLastReadMessageIfActive(ChatParticipant participant, Set<String> activeUsers) {
        boolean isActive = activeUsers.contains(participant.getUserId().toString());
        if (!isActive) {
            return;
        }
        
        participant.setLastReadMessage(OffsetDateTime.now());
    }
    
    private void updateUnreadCountIfInactive(ChatParticipant participant, Set<String> activeUsers) {
        boolean isActive = activeUsers.contains(participant.getUserId().toString());
        if (isActive) {
            return;
        }
        participant.setUnreadMessages(participant.getUnreadMessages() + 1);
    }
    
    private ChatSummaryDto buildSummaryDto(
            Chat chat,
            Message message,
            ChatParticipant currentParticipant,
            List<ChatParticipant> allParticipants) {
        
        if (chat.getChatType() != ChatTypeEnum.PRIVATE) {
            return ChatSummaryDto.from(chat, message, currentParticipant.getUnreadMessages(), storagePort);
        }

        ChatParticipant other = findOtherParticipant(currentParticipant, allParticipants);
        return ChatSummaryDto.fromPrivateChat(chat, message, currentParticipant.getUnreadMessages(), other.getUserDisplayName(), other.getUserAvatarKey(), storagePort);
    }
    
    private ChatParticipant findOtherParticipant(ChatParticipant current, List<ChatParticipant> all) {
        return all.stream()
                .filter(p -> !p.getUserId().equals(current.getUserId()))
                .findFirst()
                .orElse(current);
    }
}
