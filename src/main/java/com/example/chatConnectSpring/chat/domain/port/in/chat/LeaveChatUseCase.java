package com.example.chatConnectSpring.chat.domain.port.in.chat;

import java.util.UUID;

public interface LeaveChatUseCase {
    void leave(UUID userId, UUID chatId);
}
