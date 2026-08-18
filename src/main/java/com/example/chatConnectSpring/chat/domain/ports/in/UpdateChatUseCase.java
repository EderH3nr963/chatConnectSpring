package com.example.chatConnectSpring.chat.domain.ports.in;

import com.example.chatConnectSpring.chat.application.commands.UpdateChatCommand;
import com.example.chatConnectSpring.chat.domain.model.chat.Chat;

import java.util.UUID;

public interface UpdateChatUseCase {
    Chat update(UUID userId, UpdateChatCommand command);
}
