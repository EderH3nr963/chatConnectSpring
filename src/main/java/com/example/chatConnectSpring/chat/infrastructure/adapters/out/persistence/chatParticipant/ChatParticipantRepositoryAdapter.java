package com.example.chatConnectSpring.chat.infrastructure.adapters.out.persistence.chatParticipant;

import com.example.chatConnectSpring.chat.infrastructure.mapper.ChatParticipantMapper;
import com.example.chatConnectSpring.chat.domain.model.ChatParticipant;
import com.example.chatConnectSpring.chat.domain.port.out.chat.ChatParticipantRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ChatParticipantRepositoryAdapter implements
        ChatParticipantRepository
{
    private final ChatParticipantJpaRepository chatParticipantJpaRepository;
    
    public ChatParticipantRepositoryAdapter(
            ChatParticipantJpaRepository chatParticipantJpaRepository
    ) {
        this.chatParticipantJpaRepository = chatParticipantJpaRepository;
    }
    
    @Override
    public ChatParticipant addParticipant(ChatParticipant participant) {
        ChatParticipantEntity saved = chatParticipantJpaRepository.save(ChatParticipantMapper.toEntity(participant));
        return ChatParticipantMapper.toDomain(saved);
    }

    @Override
    public ChatParticipant getParticipantById(UUID id) {
        return ChatParticipantMapper.toDomain(chatParticipantJpaRepository.findById(id).orElse(null));
    }

    @Override
    public ChatParticipant getParticipantByChatIdAndUserId(UUID chatId, UUID userId) {
        return ChatParticipantMapper.toDomain(
                chatParticipantJpaRepository.findByChatIdAndUserId(chatId, userId).orElse(null)
        );
    }

    @Override
    public void removeParticipant(UUID participantId) {
        chatParticipantJpaRepository.deleteById(participantId);
    }

    @Override
    public long countAdminsByChatId(UUID chatId) {
        return chatParticipantJpaRepository.countAdminsByChatId(chatId);
    }

    @Override
    public ChatParticipant findOldestNonAdminParticipant(UUID chatId) {
        return ChatParticipantMapper.toDomain(
                chatParticipantJpaRepository.findOldestNonAdminParticipant(chatId).orElse(null)
        );
    }

    @Override
    public void updateRole(UUID participantId, ParticipantRole participantRole) {
        chatParticipantJpaRepository.findById(participantId).ifPresent(entity -> {
            entity.setParticipantRole(participantRole);
            chatParticipantJpaRepository.save(entity);
        });
    }
}
