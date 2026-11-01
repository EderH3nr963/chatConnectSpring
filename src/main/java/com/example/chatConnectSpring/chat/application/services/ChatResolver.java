package com.example.chatConnectSpring.chat.application.services;

import com.example.chatConnectSpring.chat.application.commands.SendMessageCommand;
import com.example.chatConnectSpring.chat.application.exceptions.ChatNotFoundException;
import com.example.chatConnectSpring.chat.application.exceptions.MessageAccessDeniedException;
import com.example.chatConnectSpring.chat.domain.model.chat.Chat;
import com.example.chatConnectSpring.chat.domain.model.chat.ChatTypeEnum;
import com.example.chatConnectSpring.chat.domain.model.chatParticipant.ChatParticipant;
import com.example.chatConnectSpring.chat.domain.ports.in.ChatUseCase;
import com.example.chatConnectSpring.chat.domain.ports.out.ChatParticipantRepository;
import com.example.chatConnectSpring.chat.domain.ports.out.ChatRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ChatResolver {
    private final ChatRepository chatRepository;
    private final ChatUseCase chatUseCase;
    private final ChatParticipantRepository chatParticipantRepository;
    
    public ChatResolver(ChatRepository chatRepository, ChatUseCase chatUseCase, ChatParticipantRepository chatParticipantRepository) {
        this.chatRepository = chatRepository;
        this.chatUseCase = chatUseCase;
        this.chatParticipantRepository = chatParticipantRepository;
    }
    
    public UUID resolveChatId(UUID userId, SendMessageCommand command) {
        if (command.chatId() != null) {
            return resolveExistingChat(userId, command.chatId());
        }
        if (command.targetId() != null) {
            return resolvePrivateChat(userId, command.targetId());
        }
        throw new IllegalArgumentException("Either chatId or targetId must be provided.");
    }
    
    private UUID resolveExistingChat(UUID userId, UUID chatId) {
        Chat chat = chatRepository.findById(chatId);
        if (chat == null) {
            throw new ChatNotFoundException("Chat not found for id: " + chatId);
        }

        if (chat.isBlocked()) {
            throw new MessageAccessDeniedException("Chat is blocked.");
        }

        if (chat.getChatType() != ChatTypeEnum.PRIVATE) {
            return chat.getId();
        }
        
        ChatParticipant other = chatParticipantRepository.findOtherParticipantInPrivateChat(chatId, userId);
        if (other != null) {
            ensureCanSendMessage(userId, other.getUserId());
        }
        
        return chat.getId();
    }
    
    private UUID resolvePrivateChat(UUID userId, UUID targetId) {
        ensureCanSendMessage(userId, targetId);

        Chat chat = chatRepository.findPrivateChat(userId, targetId);
        if (chat != null && chat.isBlocked()) {
            throw new MessageAccessDeniedException("Chat is blocked.");
        }
        if (chat == null) {
            chat = chatUseCase.createPrivateChat(userId, targetId);
        }
        
        return chat.getId();
    }
    
    private void ensureCanSendMessage(UUID senderId, UUID recipientId) {
        if (!chatRepository.canSendMessageInPrivateChat(senderId, recipientId)) {
            throw new MessageAccessDeniedException("User cannot send private message to this recipient.");
        }
    }
}