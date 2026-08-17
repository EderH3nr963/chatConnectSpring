package com.example.chatConnectSpring.chat.domain.port.in.chat;

import com.example.chatConnectSpring.chat.application.command.UpdateTitleChatCommand;
import com.example.chatConnectSpring.chat.domain.model.Chat;

public interface UpdateTitleChatUseCase {
    Chat updateTitle(UpdateTitleChatCommand command);
}
