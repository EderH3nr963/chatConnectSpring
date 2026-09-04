package com.example.chatConnectSpring.chat.domain.ports.in;

import java.util.UUID;

public interface DeleteMessagesByChatIdUseCase {
    void deleteByChatId(UUID chatId);
}
