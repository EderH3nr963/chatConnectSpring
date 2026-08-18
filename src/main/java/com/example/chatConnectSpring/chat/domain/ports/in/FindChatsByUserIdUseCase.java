package com.example.chatConnectSpring.chat.domain.ports.in;

import com.example.chatConnectSpring.chat.domain.model.chat.Chat;

import java.util.List;
import java.util.UUID;

public interface FindChatsByUserIdUseCase {
    List<Chat> findChatsByUSerId(UUID userId);
}
