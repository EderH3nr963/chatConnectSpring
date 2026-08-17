package com.example.chatConnectSpring.chat.domain.port.out.chat;

import com.example.chatConnectSpring.chat.domain.model.ChatParticipant;

import java.util.UUID;

public interface ChatParticipantRepository {
    ChatParticipant addParticipant(ChatParticipant participant);
    
    ChatParticipant getParticipantById(UUID id);

    ChatParticipant getParticipantByChatIdAndUserId(UUID chatId, UUID userId);

    void removeParticipant(UUID participantId);

    long countAdminsByChatId(UUID chatId);

    ChatParticipant findOldestNonAdminParticipant(UUID chatId);

    void updateRole(UUID participantId, com.example.chatConnectSpring.chat.infrastructure.adapters.out.persistence.chatParticipant.ParticipantRole participantRole);
}
