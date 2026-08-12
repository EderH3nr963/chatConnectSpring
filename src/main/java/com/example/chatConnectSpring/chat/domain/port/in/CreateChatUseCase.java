package com.example.chatConnectSpring.chat.domain.port.in;

import com.example.chatConnectSpring.chat.domain.model.Chat;
import com.example.chatConnectSpring.chat.infrastructure.adapters.out.persistence.chat.ChatTypeEnum;

import java.util.UUID;

public interface CreateChatUseCase {
    Chat create(UUID createdByUserId, String title, ChatTypeEnum type);
}
