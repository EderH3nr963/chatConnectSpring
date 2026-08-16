package com.example.chatConnectSpring.chat.domain.port.in.chat;

import com.example.chatConnectSpring.chat.domain.model.Chat;

import java.util.UUID;

public interface FindChatByIdUseCase {
    Chat findById(UUID chatId);
}
