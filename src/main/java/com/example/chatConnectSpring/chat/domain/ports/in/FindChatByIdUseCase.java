package com.example.chatConnectSpring.chat.domain.ports.in;

import com.example.chatConnectSpring.chat.domain.model.chat.Chat;

import java.util.UUID;

public interface FindChatByIdUseCase {
    Chat findChatById(UUID userId, UUID chatId);
}
