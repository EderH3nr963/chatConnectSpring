package com.example.chatConnectSpring.chat.domain.ports.in;

import com.example.chatConnectSpring.chat.application.commands.CreateGroupChatCommand;
import com.example.chatConnectSpring.chat.domain.model.chat.Chat;

import java.util.UUID;

public interface CreateGroupChatUseCase {
    Chat createGroupChat(UUID userId, CreateGroupChatCommand command);
}
