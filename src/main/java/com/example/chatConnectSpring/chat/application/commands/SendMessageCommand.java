package com.example.chatConnectSpring.chat.application.commands;

import com.example.chatConnectSpring.chat.application.exceptions.InvalidMessageException;
import com.example.chatConnectSpring.chat.domain.model.message.MessageType;

import java.util.UUID;

public record SendMessageCommand(
        UUID targetId,
        UUID chatId,
        String content,
        AttachmentInfo attachmentInfo,
        MessageType type,
        UUID replyId
) {
    public SendMessageCommand(UUID targetId, UUID chatId, String content, AttachmentInfo attachmentInfo, MessageType type) {
        this(targetId, chatId, content, attachmentInfo, type, null);
    }

    public void validate() {
        if (this == null) {
            throw new InvalidMessageException("Message content cannot be empty.");
        }
        boolean contentBlank = content == null || content.trim().isEmpty();
        if (contentBlank && attachmentInfo == null) {
            throw new InvalidMessageException("Message content cannot be empty.");
        }

        boolean isMedia = type != MessageType.TEXT;
        if (isMedia && (attachmentInfo == null || !type.isValidContentType(attachmentInfo.contentType()))) {
            throw new InvalidMessageException("Invalid or missing content type for " + type);
        }
    }
}

