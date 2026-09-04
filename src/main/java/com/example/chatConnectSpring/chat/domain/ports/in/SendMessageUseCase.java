package com.example.chatConnectSpring.chat.domain.ports.in;

import com.example.chatConnectSpring.chat.application.commands.SendMessageCommand;
import com.example.chatConnectSpring.chat.domain.model.Message;

import java.util.UUID;

public interface SendMessageUseCase {
    Message send(UUID userId, SendMessageCommand command);
}
