package com.example.chatConnectSpring.chat.domain.port.in;

import com.example.chatConnectSpring.chat.domain.model.ChatMessage;

import java.util.UUID;

public interface DeleteMessageUseCase {
    ChatMessage deleteMessage(UUID chatId, UUID messageId, UUID userId);
}
