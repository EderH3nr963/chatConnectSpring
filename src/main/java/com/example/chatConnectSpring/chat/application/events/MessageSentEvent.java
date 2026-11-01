package com.example.chatConnectSpring.chat.application.events;

import com.example.chatConnectSpring.chat.domain.model.chat.Chat;
import com.example.chatConnectSpring.chat.domain.model.message.Message;

import java.util.UUID;

public record MessageSentEvent(
        UUID messageId
)
{
}
