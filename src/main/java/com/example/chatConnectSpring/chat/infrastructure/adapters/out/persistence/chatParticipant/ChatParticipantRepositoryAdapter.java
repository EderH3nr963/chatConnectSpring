package com.example.chatConnectSpring.chat.infrastructure.adapters.out.persistence.chatParticipant;

import com.example.chatConnectSpring.chat.application.mapper.ChatParticipantMapper;
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
    @Transactional
    public ChatParticipant save(ChatParticipant participant) {
        ChatParticipantEntity entity = ChatParticipantMapper.toEntity(participant);
        entity.setJoinedAt(entity.getJoinedAt() == null ? OffsetDateTime.now() : entity.getJoinedAt());

        return ChatParticipantMapper.toDomain(chatParticipantJpaRepository.save(entity));
    }

    @Override
    @Transactional
    public List<ChatParticipant> saveAll(Collection<ChatParticipant> participants) {
        List<ChatParticipantEntity> entities = participants.stream()
                .map(ChatParticipantMapper::toEntity)
                .peek(entity -> {
                    if (entity.getJoinedAt() == null) {
                        entity.setJoinedAt(OffsetDateTime.now());
                    }
                })
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
    @Transactional
    public void deleteById(UUID participantId) {
        chatParticipantJpaRepository.deleteById(participantId);
    }

    @Override
    @Transactional
    public void deleteByUserIdAndChatId(UUID userId, UUID chatId) {
        chatParticipantJpaRepository.deleteByUserIdAndChatId(userId, chatId);
    }
}
