package com.example.chatConnectSpring.chat.infrastructure.adapters.out.persistence.chat;

import com.example.chatConnectSpring.chat.application.mapper.ChatMapper;
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
    @Transactional
    public Chat save(Chat chat) {
        ChatEntity entity = ChatMapper.toEntity(chat);
        entity.setCreatedAt(entity.getCreatedAt() == null ? OffsetDateTime.now() : entity.getCreatedAt());
        entity.setUpdatedAt(entity.getUpdatedAt() == null ? OffsetDateTime.now() : entity.getUpdatedAt());

        return ChatMapper.toDomain(chatJpaRepository.save(entity));
    }

    @Override
    @Transactional
    public Chat update(Chat chat) {
        if (chat == null || chat.getId() == null) {
            return null;
        }

        chatJpaRepository.updateChat(
                chat.getId(),
                chat.getTitle(),
                chat.getDescription(),
                chat.getChatType()
        );

        return findById(chat.getId());
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
    @Transactional
    public void delete(UUID chatId) {
        chatJpaRepository.deleteById(chatId);
    }
}
