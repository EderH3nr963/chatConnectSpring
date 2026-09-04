package com.example.chatConnectSpring.chat.domain.ports.in;

import java.util.UUID;

public interface DeleteMessageUseCase {
    void delete(UUID userId, UUID messageId);
}
