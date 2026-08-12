package com.example.chatConnectSpring.chat.domain.port.out;

import com.example.chatConnectSpring.chat.domain.model.Chat;

import java.util.List;
import java.util.UUID;

public interface ChatRepository {
    Chat findById(UUID chatId);
    
    List<Chat> findByUserId(UUID userId);

    Chat create(Chat chat);
}
