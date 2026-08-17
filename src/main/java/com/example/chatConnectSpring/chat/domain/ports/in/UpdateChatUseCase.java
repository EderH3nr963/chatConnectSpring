package com.example.chatConnectSpring.chat.domain.ports.in;

import com.example.chatConnectSpring.chat.application.commands.UpdateChatCommand;
import com.example.chatConnectSpring.chat.domain.model.chat.Chat;

public interface UpdateChatUseCase {
    Chat update(UpdateChatCommand command);
}
