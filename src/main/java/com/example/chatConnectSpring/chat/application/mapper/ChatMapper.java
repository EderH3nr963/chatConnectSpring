package com.example.chatConnectSpring.chat.application.mapper;

import com.example.chatConnectSpring.chat.domain.model.chat.Chat;
import com.example.chatConnectSpring.chat.infrastructure.adapters.out.persistence.chat.ChatEntity;

public class ChatMapper {
    public static ChatEntity toEntity(Chat chat) {
        if (chat == null) {
            return null;
        }

        ChatEntity entity = new ChatEntity();
        entity.setId(chat.getId());
        entity.setTitle(chat.getTitle());
        entity.setDescription(chat.getDescription());
        entity.setChatType(chat.getChatType());
        entity.setCreatedAt(chat.getCreatedAt());
        entity.setUpdatedAt(chat.getUpdatedAt());
        return entity;
    }

    public static Chat toDomain(ChatEntity entity) {
        if (entity == null) {
            return null;
        }

        Chat domain = new Chat();
        domain.setId(entity.getId());
        domain.setTitle(entity.getTitle());
        domain.setDescription(entity.getDescription());
        domain.setChatType(entity.getChatType());
        domain.setCreatedAt(entity.getCreatedAt());
        domain.setUpdatedAt(entity.getUpdatedAt());
        return domain;
    }
}
