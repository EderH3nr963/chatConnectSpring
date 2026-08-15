package com.example.chatConnectSpring.chat.domain.port.in;

import com.example.chatConnectSpring.chat.domain.model.ChatMessage;

import java.util.UUID;

public interface SendMessageUseCase {
    ChatMessage sendMessage(UUID chatId, UUID userId, String content);
}
