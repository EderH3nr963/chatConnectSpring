package com.example.chatConnectSpring.chat.domain.ports.in;

import java.util.UUID;

public interface DeleteChatUseCase {
    void delete(UUID userId, UUID chatId);
}
