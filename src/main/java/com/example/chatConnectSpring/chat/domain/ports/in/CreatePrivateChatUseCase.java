package com.example.chatConnectSpring.chat.domain.ports.in;

import com.example.chatConnectSpring.chat.application.commands.CreatePrivateChatCommand;
import com.example.chatConnectSpring.chat.domain.model.chat.Chat;

import java.util.UUID;

public interface CreatePrivateChatUseCase {
    Chat createPrivateChat(UUID userId, CreatePrivateChatCommand command);
}
