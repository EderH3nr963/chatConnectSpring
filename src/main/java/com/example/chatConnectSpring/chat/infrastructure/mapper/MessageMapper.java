package com.example.chatConnectSpring.chat.infrastructure.mapper;

import com.example.chatConnectSpring.chat.domain.model.message.Message;
import com.example.chatConnectSpring.chat.domain.ports.out.StoragePort;
import com.example.chatConnectSpring.chat.infrastructure.adapters.in.http.dto.response.MessageReplyDTO;
import com.example.chatConnectSpring.chat.infrastructure.adapters.in.http.dto.response.MessageResponseDTO;
import com.example.chatConnectSpring.chat.infrastructure.adapters.out.persistence.message.MessageEntity;
import com.example.chatConnectSpring.user.infrastructure.mapper.UserMapper;

import java.time.Duration;

public class MessageMapper {

    public static MessageEntity toEntity(Message domain) {
        if (domain == null) return null;
        MessageEntity entity = new MessageEntity();
        entity.setId(domain.getId());
        entity.setChatId(domain.getChatId());
        entity.setSenderId(domain.getSenderId());
        entity.setContent(domain.getContent());
        entity.setStatus(domain.getStatus());
        entity.setType(domain.getType());
        entity.setFileName(domain.getFileName());
        entity.setFileMimeType(domain.getFileMimeType());
        entity.setFileSize(domain.getFileSize());
        entity.setFileS3Key(domain.getFileS3Key());
        entity.setReplyId(domain.getReplyId());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    public static Message toDomain(MessageEntity entity) {
        if (entity == null) return null;
        Message domain = new Message();
        domain.setId(entity.getId());
        domain.setChatId(entity.getChatId());
        domain.setChat(ChatMapper.toDomain(entity.getChat()));
        domain.setSenderId(entity.getSenderId());
        domain.setSender(UserMapper.toDomain(entity.getSender()));
        domain.setContent(entity.getContent());
        domain.setStatus(entity.getStatus());
        domain.setType(entity.getType());
        domain.setFileName(entity.getFileName());
        domain.setFileMimeType(entity.getFileMimeType());
        domain.setFileSize(entity.getFileSize());
        domain.setFileS3Key(entity.getFileS3Key());
        domain.setReplyId(entity.getReplyId());
        if (entity.getReply() != null) {
            domain.setReply(toDomain(entity.getReply()));
        }
        domain.setCreatedAt(entity.getCreatedAt());
        domain.setUpdatedAt(entity.getUpdatedAt());
        return domain;
    }

    public static MessageResponseDTO toDTO(Message domain) {
        return toDTO(domain, null);
    }

    public static MessageResponseDTO toDTO(Message domain, StoragePort storagePort) {
        if (domain == null) return null;
        String downloadUrl = resolveDownloadUrl(domain.getFileS3Key(), storagePort);
        var replyDto = toReplyDTO(domain.getReply(), storagePort);
        return new MessageResponseDTO(
                domain.getId(),
                UserMapper.toSummaryDTO(domain.getSender()),
                domain.getContent(),
                ChatMapper.toDTO(domain.getChat()),
                domain.getCreatedAt(),
                domain.getUpdatedAt(),
                domain.getType(),
                domain.getStatus(),
                domain.getFileName(),
                domain.getFileS3Key(),
                domain.getFileMimeType(),
                domain.getFileSize(),
                downloadUrl,
                domain.getReplyId(),
                replyDto
        );
    }

    private static MessageReplyDTO toReplyDTO(Message reply, StoragePort storagePort) {
        if (reply == null) return null;
        String replyDownloadUrl = resolveDownloadUrl(reply.getFileS3Key(), storagePort);
        return new MessageReplyDTO(
                reply.getId(),
                reply.getContent(),
                reply.getType(),
                reply.getFileName(),
                reply.getFileMimeType(),
                reply.getFileS3Key(),
                replyDownloadUrl,
                UserMapper.toSummaryDTO(reply.getSender()),
                reply.getCreatedAt()
        );
    }

    private static String resolveDownloadUrl(String fileS3Key, StoragePort storagePort) {
        if (storagePort == null || fileS3Key == null || fileS3Key.isBlank()) return null;
        if (fileS3Key.startsWith("http://") || fileS3Key.startsWith("https://")) return fileS3Key;
        try {
            return storagePort.generatePresignedDownloadUrl(fileS3Key, Duration.ofMinutes(60));
        } catch (Exception ignored) {
            return null;
        }
    }
}
