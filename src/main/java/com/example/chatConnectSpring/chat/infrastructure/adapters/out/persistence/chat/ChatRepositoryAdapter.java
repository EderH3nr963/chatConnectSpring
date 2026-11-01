package com.example.chatConnectSpring.chat.infrastructure.adapters.out.persistence.chat;

import com.example.chatConnectSpring.chat.domain.ports.out.StoragePort;
import com.example.chatConnectSpring.chat.domain.ports.out.projections.ChatSummaryDto;
import com.example.chatConnectSpring.chat.infrastructure.mapper.ChatMapper;
import com.example.chatConnectSpring.chat.domain.model.chat.Chat;
import com.example.chatConnectSpring.chat.domain.ports.out.ChatRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class ChatRepositoryAdapter implements ChatRepository {

    private final ChatJpaRepository chatJpaRepository;
    private final StoragePort storagePort;

    public ChatRepositoryAdapter(ChatJpaRepository chatJpaRepository, StoragePort storagePort) {
        this.chatJpaRepository = chatJpaRepository;
        this.storagePort = storagePort;
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
    public List<ChatSummaryDto> findChatSummaries(UUID userId) {
        return chatJpaRepository
                .findChatSummariesByUserId(userId)
                .stream()
                .map(p -> p.toDto(storagePort))
                .toList();
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
    public void blockPrivateChat(UUID chatId) {
        chatJpaRepository.markBlocked(chatId);
    }

    @Override
    public void unblockPrivateChat(UUID chatId) {
        chatJpaRepository.clearBlocked(chatId);
    }

    @Override
    public void delete(UUID chatId) {
        chatJpaRepository.deleteById(chatId);
    }
    
    @Override
    public boolean canSendMessageInPrivateChat(UUID senderId, UUID targetId) {
        return chatJpaRepository.canSendMessageInPrivateChat(senderId, targetId);
    }
    
    @Override
    public Chat findPrivateChat(UUID senderId, UUID targetId) {
        ChatEntity entity = chatJpaRepository.findPrivateChat(senderId, targetId).orElse(null);
        
        return ChatMapper.toDomain(entity);
    }
}
