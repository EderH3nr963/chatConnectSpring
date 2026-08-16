package com.example.chatConnectSpring.chat.domain.port.in.chat;

import com.example.chatConnectSpring.chat.application.command.CreateChatCommand;
import com.example.chatConnectSpring.chat.domain.model.Chat;

public interface CreateChatUseCase {
    Chat create(CreateChatCommand command);
}
