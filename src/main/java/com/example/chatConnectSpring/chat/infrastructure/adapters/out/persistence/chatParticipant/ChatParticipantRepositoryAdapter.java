package com.example.chatConnectSpring.chat.infrastructure.adapters.out.persistence.chatParticipant;

import com.example.chatConnectSpring.chat.infrastructure.mapper.ChatParticipantMapper;
import com.example.chatConnectSpring.chat.domain.model.chatParticipant.ChatParticipant;
import com.example.chatConnectSpring.chat.domain.ports.out.ChatParticipantRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Component
public class ChatParticipantRepositoryAdapter implements ChatParticipantRepository {

    private final ChatParticipantJpaRepository chatParticipantJpaRepository;

    public ChatParticipantRepositoryAdapter(ChatParticipantJpaRepository chatParticipantJpaRepository) {
        this.chatParticipantJpaRepository = chatParticipantJpaRepository;
    }

    @Override
    public ChatParticipant save(ChatParticipant participant) {
        if (participant == null) {
            return null;
        }
        ChatParticipantEntity entity = ChatParticipantMapper.toEntity(participant);
        return ChatParticipantMapper.toDomain(chatParticipantJpaRepository.save(entity));
    }

    @Override
    public List<ChatParticipant> saveAll(Collection<ChatParticipant> participants) {
        if (participants == null || participants.isEmpty()) {
            return List.of();
        }
        List<ChatParticipantEntity> entities = participants.stream()
                .map(ChatParticipantMapper::toEntity)
                .toList();

        return chatParticipantJpaRepository.saveAll(entities).stream()
                .map(ChatParticipantMapper::toDomain)
                .toList();
    }

    @Override
    public ChatParticipant findByUserIdAndChatId(UUID userId, UUID chatId) {
        return chatParticipantJpaRepository.findByUserIdAndChatId(userId, chatId)
                .map(ChatParticipantMapper::toDomain)
                .orElse(null);
    }

    @Override
    public List<ChatParticipant> findByChatId(UUID chatId) {
        return chatParticipantJpaRepository.findByChatId(chatId)
                .stream()
                .map(ChatParticipantMapper::toDomain)
                .toList();
    }

    @Override
    public ChatParticipant findById(UUID participantId) {
        return chatParticipantJpaRepository.findById(participantId)
                .map(ChatParticipantMapper::toDomain)
                .orElse(null);
    }

    @Override
    public void deleteById(UUID participantId) {
        chatParticipantJpaRepository.deleteById(participantId);
    }
    
    @Override
    public void deleteAllByChatId(UUID chatId) {
        chatParticipantJpaRepository.deleteAllByChatId(chatId);
    }
    
    @Override
    public void deleteByUserIdAndChatId(UUID userId, UUID chatId) {
        chatParticipantJpaRepository.deleteByUserIdAndChatId(userId, chatId);
    }
}
