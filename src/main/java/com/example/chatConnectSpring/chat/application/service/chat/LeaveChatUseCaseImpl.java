package com.example.chatConnectSpring.chat.application.service.chat;

import com.example.chatConnectSpring.chat.application.exception.ChatNotFoundException;
import com.example.chatConnectSpring.chat.domain.model.Chat;
import com.example.chatConnectSpring.chat.domain.model.ChatParticipant;
import com.example.chatConnectSpring.chat.domain.port.in.chat.LeaveChatUseCase;
import com.example.chatConnectSpring.chat.domain.port.out.chat.ChatParticipantRepository;
import com.example.chatConnectSpring.chat.domain.port.out.chat.ChatRepository;
import com.example.chatConnectSpring.chat.infrastructure.adapters.out.persistence.chatParticipant.ParticipantRole;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class LeaveChatUseCaseImpl implements LeaveChatUseCase {
    private final ChatRepository chatRepository;
    private final ChatParticipantRepository chatParticipantRepository;

    public LeaveChatUseCaseImpl(
            ChatRepository chatRepository,
            ChatParticipantRepository chatParticipantRepository
    ) {
        this.chatRepository = chatRepository;
        this.chatParticipantRepository = chatParticipantRepository;
    }
    
    @Override
    @Transactional
    public void leave(UUID userId, UUID chatId) {
        
        Chat chat = chatRepository.findById(chatId);
        
        if (chat == null) {
            throw new ChatNotFoundException("Chat não encontrado");
        }
        
        ChatParticipant participant =
                chatParticipantRepository
                        .getParticipantByChatIdAndUserId(chatId, userId);
        
        if (participant == null) {
            throw new ChatNotFoundException("Usuário não participa deste chat");
        }
        
        boolean wasAdmin =
                participant.getParticipantRole() == ParticipantRole.ADMIN;
        
        chatParticipantRepository.removeParticipant(participant.getId());
        
        if (!wasAdmin) {
            return;
        }
        
        if (chatParticipantRepository.countAdminsByChatId(chatId) > 0) {
            return;
        }
        
        ChatParticipant newAdmin =
                chatParticipantRepository.findOldestNonAdminParticipant(chatId);
        
        if (newAdmin == null) {
            chatRepository.delete(chatId);
            return;
        }
        
        chatParticipantRepository.updateRole(
                newAdmin.getId(),
                ParticipantRole.ADMIN
        );
    }
}
