package com.example.chatConnectSpring.chat.domain.ports.in;

import java.util.UUID;

public interface UpdateAvatarChatUseCase {
    void execute(UUID chatId, String objectKey);
}
