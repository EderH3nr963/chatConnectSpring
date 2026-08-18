package com.example.chatConnectSpring.chat.domain.ports.out;

import com.example.chatConnectSpring.chat.domain.model.chat.Chat;

import java.util.List;
import java.util.UUID;

public interface ChatRepository {
    Chat save(Chat chat);

    Chat update(Chat chat);

    Chat findById(UUID chatId);

    List<Chat> findChatByUserId(UUID userId);

    void delete(UUID chatId);
}
