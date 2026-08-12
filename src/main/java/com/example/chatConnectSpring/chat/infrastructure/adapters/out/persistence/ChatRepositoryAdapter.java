package com.example.chatConnectSpring.chat.infrastructure.adapters.out.persistence;

import com.example.chatConnectSpring.chat.application.mapper.ChatMapper;
import com.example.chatConnectSpring.chat.application.mapper.ChatParticipantMapper;
import com.example.chatConnectSpring.chat.domain.model.Chat;
import com.example.chatConnectSpring.chat.domain.model.ChatParticipant;
import com.example.chatConnectSpring.chat.domain.port.out.ChatParticipantRepository;
import com.example.chatConnectSpring.chat.domain.port.out.ChatRepository;
import com.example.chatConnectSpring.chat.infrastructure.adapters.out.persistence.chat.ChatEntity;
import com.example.chatConnectSpring.chat.infrastructure.adapters.out.persistence.chat.ChatJpaRepository;
import com.example.chatConnectSpring.chat.infrastructure.adapters.out.persistence.chatParticipant.ChatParticipantEntity;
import com.example.chatConnectSpring.chat.infrastructure.adapters.out.persistence.chatParticipant.ChatParticipantJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class ChatRepositoryAdapter implements
        ChatParticipantRepository,
        ChatRepository
{

    private final ChatJpaRepository chatJpaRepository;
    private final ChatParticipantJpaRepository chatParticipantJpaRepository;

    public ChatRepositoryAdapter(
            ChatJpaRepository chatJpaRepository,
            ChatParticipantJpaRepository chatParticipantJpaRepository
    ) {
        this.chatJpaRepository = chatJpaRepository;
        this.chatParticipantJpaRepository = chatParticipantJpaRepository;
    }

    @Override
    public Chat create(Chat chat) {
        ChatEntity saved = chatJpaRepository.save(ChatMapper.toEntity(chat));
        return ChatMapper.toDomain(saved);
    }

    @Override
    public Chat findById(UUID chatId) {
        return ChatMapper.toDomain(chatJpaRepository.findById(chatId).orElse(null));
    }

    @Override
    public List<Chat> findByUserId(UUID userId) {
        return chatJpaRepository.findByCreatedByUserId(userId).stream().map(ChatMapper::toDomain).toList();
    }

    @Override
    public ChatParticipant addParticipant(ChatParticipant participant) {
        ChatParticipantEntity saved = chatParticipantJpaRepository.save(ChatParticipantMapper.toEntity(participant));
        return ChatParticipantMapper.toDomain(saved);
    }
}
