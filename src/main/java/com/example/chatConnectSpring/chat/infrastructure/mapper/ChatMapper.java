package com.example.chatConnectSpring.chat.infrastructure.mapper;

import com.example.chatConnectSpring.chat.domain.model.chat.Chat;
import com.example.chatConnectSpring.chat.domain.ports.out.StoragePort;
import com.example.chatConnectSpring.chat.infrastructure.adapters.out.persistence.chat.ChatEntity;
import com.example.chatConnectSpring.chat.infrastructure.adapters.in.http.dto.response.ChatResponseDTO;

import java.time.Duration;

public class ChatMapper {
    private ChatMapper() {}

    public static ChatEntity toEntity(Chat chat) {
        if (chat == null) {
            return null;
        }
        ChatEntity entity = new ChatEntity();
        entity.setId(chat.getId());
        entity.setName(chat.getName());
        entity.setAvatarKey(chat.getAvatarKey());
        entity.setDescription(chat.getDescription());
        entity.setChatType(chat.getChatType());
        entity.setCreatedAt(chat.getCreatedAt());
        entity.setUpdatedAt(chat.getUpdatedAt());
        entity.setBlocked(chat.isBlocked());
        entity.setBlockedAt(chat.getBlockedAt());
        return entity;
    }

    public static Chat toDomain(ChatEntity entity) {
        if (entity == null) {
            return null;
        }
        Chat domain = new Chat();
        domain.setId(entity.getId());
        domain.setName(entity.getName());
        domain.setAvatarKey(entity.getAvatarKey());
        domain.setDescription(entity.getDescription());
        domain.setChatType(entity.getChatType());
        domain.setCreatedAt(entity.getCreatedAt());
        domain.setUpdatedAt(entity.getUpdatedAt());
        domain.setBlocked(entity.isBlocked());
        domain.setBlockedAt(entity.getBlockedAt());
        return domain;
    }

    public static ChatResponseDTO toDTO(Chat chat) {
        return toDTO(chat, (StoragePort) null);
    }

    public static ChatResponseDTO toDTO(Chat chat, StoragePort storagePort) {
        if (chat == null) {
            return null;
        }
        String avatarUrl = resolveAvatarUrl(chat.getAvatarKey(), storagePort);
        String avatarKeyForDto = avatarUrl != null ? avatarUrl : chat.getAvatarKey();
        
        return new ChatResponseDTO(
                chat.getId(),
                chat.getName(),
                avatarKeyForDto,
                chat.getDescription(),
                chat.getChatType(),
                chat.getCreatedAt(),
                chat.getUpdatedAt()
        );
    }

    private static String resolveAvatarUrl(String avatarKey, StoragePort storagePort) {
        if (storagePort == null || avatarKey == null || avatarKey.isBlank()) return null;
        if (avatarKey.startsWith("http://") || avatarKey.startsWith("https://")) return avatarKey;
        try {
            return storagePort.generatePresignedDownloadUrl(avatarKey, Duration.ofMinutes(60));
        } catch (Exception ignored) {
            return null;
        }
    }
}
