package com.example.chatConnectSpring.chat.application.mapper;

import com.example.chatConnectSpring.chat.domain.model.chatParticipant.ChatParticipant;
import com.example.chatConnectSpring.chat.infrastructure.adapters.out.persistence.chatParticipant.ChatParticipantEntity;

public class ChatParticipantMapper {
    public static ChatParticipantEntity toEntity(ChatParticipant chatParticipant) {
        if (chatParticipant == null) {
            return null;
        }

        ChatParticipantEntity entity = new ChatParticipantEntity();
        entity.setId(chatParticipant.getId());
        entity.setName(chatParticipant.getName());
        entity.setUserId(chatParticipant.getUserId());
        entity.setChatId(chatParticipant.getChatId());
        entity.setJoinedAt(chatParticipant.getJoinedAt());
        entity.setRole(chatParticipant.getRole());
        return entity;
    }

    public static ChatParticipant toDomain(ChatParticipantEntity entity) {
        if (entity == null) {
            return null;
        }

        ChatParticipant domain = new ChatParticipant();
        domain.setId(entity.getId());
        domain.setName(entity.getName());
        domain.setUserId(entity.getUserId());
        domain.setChatId(entity.getChatId());
        domain.setJoinedAt(entity.getJoinedAt());
        domain.setRole(entity.getRole());
        return domain;
    }
}
