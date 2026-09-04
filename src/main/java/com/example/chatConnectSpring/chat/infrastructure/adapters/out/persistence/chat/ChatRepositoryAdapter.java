package com.example.chatConnectSpring.chat.infrastructure.adapters.out.persistence.chat;

import com.example.chatConnectSpring.chat.infrastructure.mapper.ChatMapper;
import com.example.chatConnectSpring.chat.domain.model.chat.Chat;
import com.example.chatConnectSpring.chat.domain.ports.out.ChatRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class ChatRepositoryAdapter implements ChatRepository {

    private final ChatJpaRepository chatJpaRepository;

    public ChatRepositoryAdapter(ChatJpaRepository chatJpaRepository) {
        this.chatJpaRepository = chatJpaRepository;
    }

    @Override
    public Chat save(Chat chat) {
        if (chat == null) {
            return null;
        }
        ChatEntity entity = ChatMapper.toEntity(chat);
        return ChatMapper.toDomain(chatJpaRepository.save(entity));
    }

    @Override
    public Chat update(Chat chat) {
        return save(chat);
    }

    @Override
    public Chat findById(UUID chatId) {
        return chatJpaRepository.findById(chatId)
                .map(ChatMapper::toDomain)
                .orElse(null);
    }

    @Override
    public List<Chat> findChatByUserId(UUID userId) {
        return chatJpaRepository.findChatsByUserId(userId)
                .stream()
                .map(ChatMapper::toDomain)
                .toList();
    }

    @Override
    public void delete(UUID chatId) {
        chatJpaRepository.deleteById(chatId);
    }
}
