package com.example.chatConnectSpring.chat.infrastructure.adapters.out.persistence.chat;

import com.example.chatConnectSpring.chat.domain.model.Chat;
import com.example.chatConnectSpring.chat.domain.port.out.chat.ChatRepository;
import com.example.chatConnectSpring.chat.infrastructure.adapters.out.persistence.chatParticipant.ChatParticipantJpaRepository;
import com.example.chatConnectSpring.chat.infrastructure.mapper.ChatMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ChatRepositoryAdapter implements ChatRepository {
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
    public Chat save(Chat chat) {
        ChatEntity saved = chatJpaRepository.save(ChatMapper.toEntity(chat));
        return ChatMapper.toDomain(saved);
    }

    @Override
    public void delete(UUID chatId) {
        chatJpaRepository.deleteById(chatId);
    }
    
    @Override
    public Chat findById(UUID chatId) {
        return ChatMapper.toDomain(chatJpaRepository.findById(chatId).orElse(null));
    }
    
    @Override
    public Page<Chat> findByUserId(UUID userId, Pageable pageable) {
        return chatJpaRepository.findByUserId(userId, pageable).map(ChatMapper::toDomain);
    }
}
