package com.example.chatConnectSpring.chat.domain.ports.in;

import com.example.chatConnectSpring.chat.domain.model.Message;

import java.util.List;
import java.util.UUID;

public interface FindMessagesByChatIdUseCase {
    List<Message> findMessagesByChatId(UUID userId, UUID chatId);
}
