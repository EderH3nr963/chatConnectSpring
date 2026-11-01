package com.example.chatConnectSpring.chat.infrastructure.mapper;

import com.example.chatConnectSpring.chat.domain.model.chatParticipant.ChatParticipant;
import com.example.chatConnectSpring.chat.domain.ports.out.StoragePort;
import com.example.chatConnectSpring.chat.infrastructure.adapters.in.http.dto.response.ChatParticipantResponseDTO;
import com.example.chatConnectSpring.chat.infrastructure.adapters.out.persistence.chatParticipant.ChatParticipantEntity;
import com.example.chatConnectSpring.user.domain.port.out.UserStoragePort;
import com.example.chatConnectSpring.user.infrastructure.mapper.UserMapper;

public class ChatParticipantMapper {
    private ChatParticipantMapper() {}

    public static ChatParticipantEntity toEntity(ChatParticipant chatParticipant) {
        if (chatParticipant == null) {
            return null;
        }
        ChatParticipantEntity entity = new ChatParticipantEntity();
        entity.setId(chatParticipant.getId());
        entity.setUserId(chatParticipant.getUserId());
        entity.setChatId(chatParticipant.getChatId());
        entity.setUsername(chatParticipant.getUsername());
        entity.setLastReadMessage(chatParticipant.getLastReadMessage());
        entity.setJoinedAt(chatParticipant.getJoinedAt());
        entity.setRole(chatParticipant.getRole());
        entity.setUnreadMessages(chatParticipant.getUnreadMessages());
        entity.setMuted(chatParticipant.isMuted());
        return entity;
    }

    public static ChatParticipant toDomain(ChatParticipantEntity entity) {
        if (entity == null) {
            return null;
        }
        ChatParticipant domain = new ChatParticipant();
        domain.setId(entity.getId());
        domain.setUser(UserMapper.toDomain(entity.getUser()));
        domain.setUserId(entity.getUserId());
        domain.setChatId(entity.getChatId());
        domain.setLastReadMessage(entity.getLastReadMessage());
        domain.setUsername(entity.getUsername());
        domain.setJoinedAt(entity.getJoinedAt());
        domain.setRole(entity.getRole());
        domain.setUnreadMessages(entity.getUnreadMessages());
        domain.setMuted(entity.isMuted());
        return domain;
    }
    
    public static ChatParticipantResponseDTO toDTO(ChatParticipant participant) {
        return toDTO(participant, (StoragePort) null);
    }

    public static ChatParticipantResponseDTO toDTO(ChatParticipant participant, StoragePort storagePort) {
        if (participant == null) {
            return null;
        }
        var userDto = storagePort != null
                ? UserMapper.toSummaryDTO(participant.getUser(), storagePort)
                : UserMapper.toSummaryDTO(participant.getUser());
        return new ChatParticipantResponseDTO(
                participant.getId(),
                participant.getChatId(),
                participant.getLastReadMessage(),
                userDto,
                participant.getJoinedAt(),
                participant.getRole()
        );
    }

    static ChatParticipantResponseDTO toDTO(ChatParticipant participant, UserStoragePort userStoragePort) {
        if (participant == null) {
            return null;
        }
        var userDto = userStoragePort != null
                ? UserMapper.toSummaryDTO(participant.getUser(), userStoragePort)
                : UserMapper.toSummaryDTO(participant.getUser());
        return new ChatParticipantResponseDTO(
                participant.getId(),
                participant.getChatId(),
                participant.getLastReadMessage(),
                userDto,
                participant.getJoinedAt(),
                participant.getRole()
        );
    }
}
