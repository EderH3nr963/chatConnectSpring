package com.example.chatConnectSpring.chat.infrastructure.adapters.in.http.dto.response;

import com.example.chatConnectSpring.chat.domain.model.message.MessageType;
import com.example.chatConnectSpring.user.infrastructure.adapters.in.http.dto.response.UserSummaryResponseDTO;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Snapshot do reply dentro do MessageResponseDTO.
 * Carrega content + dados do sender + se for imagem a URL assinada.
 */
public record MessageReplyDTO(
        UUID id,
        String content,
        MessageType type,
        String fileName,
        String fileMimeType,
        String fileS3Key,
        String downloadUrl,
        UserSummaryResponseDTO sender,
        OffsetDateTime createdAt
) {}
