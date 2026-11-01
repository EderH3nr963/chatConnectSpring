package com.example.chatConnectSpring.chat.infrastructure.adapters.in.websocket.dto;

import com.example.chatConnectSpring.chat.application.commands.AttachmentInfo;
import com.example.chatConnectSpring.chat.domain.model.message.MessageType;

import java.util.UUID;

public record WebSocketSendMessageDTO(
        UUID clientMessageId,
        UUID chatId,
        UUID targetId,
        AttachmentInfo attachmentInfo,
        MessageType messageType,
        String content,
        UUID replyId
) {
    public WebSocketSendMessageDTO(UUID clientMessageId, UUID chatId, UUID targetId, AttachmentInfo attachmentInfo, MessageType messageType, String content) {
        this(clientMessageId, chatId, targetId, attachmentInfo, messageType, content, null);
    }
}
