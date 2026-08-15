package com.example.chatConnectSpring.chat.domain.port.out;

import com.example.chatConnectSpring.chat.domain.model.ChatMessage;

import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {
    ChatMessage save(ChatMessage message);
    Optional<ChatMessage> findById(UUID messageId);
}
