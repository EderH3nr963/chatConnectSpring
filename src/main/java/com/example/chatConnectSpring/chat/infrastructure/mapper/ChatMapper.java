package com.example.chatConnectSpring.chat.infrastructure.mapper;

import com.example.chatConnectSpring.chat.domain.model.Chat;
import com.example.chatConnectSpring.chat.infrastructure.adapters.out.persistence.chat.ChatEntity;

public class ChatMapper {
    public static ChatEntity toEntity(Chat chat) {
        ChatEntity entity = new ChatEntity();
        entity.setId(chat.getId());
        entity.setGroupType(chat.getType());
        entity.setTitle(chat.getTitle());
        entity.setCreatedByUserId(chat.getCreatedByUserId());
        entity.setCreatedAt(chat.getCreatedAt());
        entity.setUpdatedAt(chat.getUpdatedAt());
        entity.setActive(chat.isActive());
        return entity;
    }

    public static Chat toDomain(ChatEntity entity) {
        if (entity == null) return null;
        Chat chat = new Chat();
        chat.setId(entity.getId());
        chat.setType(entity.getGroupType());
        chat.setTitle(entity.getTitle());
        chat.setCreatedByUserId(entity.getCreatedByUserId());
        chat.setCreatedAt(entity.getCreatedAt());
        chat.setUpdatedAt(entity.getUpdatedAt());
        chat.setActive(entity.isActive());
        return chat;
    }
}
