package com.example.chatConnectSpring.chat.domain.ports.in;

import com.example.chatConnectSpring.chat.domain.model.chat.Chat;

import java.util.UUID;

public interface FindChatById {
    Chat findChatById(UUID chatId);
}
