package com.example.chatConnectSpring.chat.domain.ports.in;

import com.example.chatConnectSpring.chat.application.commands.EditMessageCommand;
import com.example.chatConnectSpring.chat.domain.model.Message;

import java.util.UUID;

public interface EditMessageUseCase {
    Message edit(UUID userId, EditMessageCommand command);
}
