package com.example.chatConnectSpring.chat.infrastructure.mapper;

import com.example.chatConnectSpring.chat.domain.model.Message;
import com.example.chatConnectSpring.chat.infrastructure.adapters.in.http.dto.response.MessageResponseDTO;
import com.example.chatConnectSpring.chat.infrastructure.adapters.out.persistence.message.MessageEntity;

public class MessageMapper {

    public static MessageEntity toEntity(Message domain) {
        if (domain == null) {
            return null;
        }

        MessageEntity entity = new MessageEntity();
        entity.setId(domain.getId());
        entity.setChatId(domain.getChatId());
        entity.setSenderId(domain.getSenderId());
        entity.setContent(domain.getContent());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    public static Message toDomain(MessageEntity entity) {
        if (entity == null) {
            return null;
        }

        Message domain = new Message();
        domain.setId(entity.getId());
        domain.setChatId(entity.getChatId());
        domain.setSenderId(entity.getSenderId());
        domain.setSender(ChatParticipantMapper.toDomain(entity.getSender()));
        domain.setContent(entity.getContent());
        domain.setCreatedAt(entity.getCreatedAt());
        domain.setUpdatedAt(entity.getUpdatedAt());
        return domain;
    }

    public static MessageResponseDTO toDTO(Message domain) {
        if (domain == null) {
            return null;
        }

        return new MessageResponseDTO(
                domain.getId(),
                domain.getChatId(),
                domain.getSenderId(),
                domain.getSender().getUsername(),
                domain.getContent(),
                domain.getCreatedAt(),
                domain.getUpdatedAt()
        );
    }
}
