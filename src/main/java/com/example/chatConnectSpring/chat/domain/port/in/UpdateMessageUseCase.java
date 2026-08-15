package com.example.chatConnectSpring.chat.domain.port.in;

import com.example.chatConnectSpring.chat.domain.model.ChatMessage;

import java.util.UUID;

public interface UpdateMessageUseCase {
    ChatMessage updateMessage(UUID chatId, UUID messageId, UUID userId, String content);
}
