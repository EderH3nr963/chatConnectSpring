package com.example.chatConnectSpring.chat.application.mapper;

import com.example.chatConnectSpring.chat.domain.model.ChatParticipant;
import com.example.chatConnectSpring.chat.infrastructure.adapters.out.persistence.chatParticipant.ChatParticipantEntity;

public class ChatParticipantMapper {
    public static ChatParticipantEntity toEntity(ChatParticipant participant) {
        ChatParticipantEntity entity = new ChatParticipantEntity();
        entity.setId(participant.getId());
        entity.setJoinedAt(participant.getJoinedAt());
        entity.setLastReadAt(participant.getLastReadAt());
        entity.setMuted(participant.isMuted());
        entity.setBlocked(participant.isBlocked());
        entity.setUserId(participant.getUserId());
        return entity;
    }

    public static ChatParticipant toDomain(ChatParticipantEntity entity) {
        if (entity == null) return null;
        ChatParticipant participant = new ChatParticipant();
        participant.setId(entity.getId());
        participant.setChatId(entity.getChat() != null ? entity.getChat().getId() : null);
        participant.setUserId(entity.getUserId());
        participant.setJoinedAt(entity.getJoinedAt());
        participant.setLastReadAt(entity.getLastReadAt());
        participant.setMuted(entity.isMuted());
        participant.setBlocked(entity.isBlocked());
        return participant;
    }
}
