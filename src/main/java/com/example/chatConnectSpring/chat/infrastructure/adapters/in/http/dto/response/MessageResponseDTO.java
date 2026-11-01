package com.example.chatConnectSpring.chat.infrastructure.adapters.in.http.dto.response;

import com.example.chatConnectSpring.chat.domain.model.message.MessageStatus;
import com.example.chatConnectSpring.chat.domain.model.message.MessageType;
import com.example.chatConnectSpring.user.infrastructure.adapters.in.http.dto.response.UserSummaryResponseDTO;

import java.time.OffsetDateTime;
import java.util.UUID;

public record MessageResponseDTO(
        UUID id,
        UserSummaryResponseDTO sender,
        String content,
        ChatResponseDTO chat,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        MessageType type,
        MessageStatus status,
        String fileName,
        String fileS3Key,
        String fileMimeType,
        Long fileSize,
        String downloadUrl,
        UUID replyId,
        MessageReplyDTO reply
) {
    public UUID chatId() {
        return chat != null ? chat.id() : null;
    }

    // compat: construtor legado sem anexo (usado em deserializacoes antigas)
    public MessageResponseDTO(UUID id, UserSummaryResponseDTO sender, String content, ChatResponseDTO chat, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this(id, sender, content, chat, createdAt, updatedAt, MessageType.TEXT, MessageStatus.SENT, null, null, null, null, null, null, null);
    }
}
