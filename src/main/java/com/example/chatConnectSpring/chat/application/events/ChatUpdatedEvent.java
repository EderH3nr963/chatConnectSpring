package com.example.chatConnectSpring.chat.application.events;

import com.example.chatConnectSpring.chat.domain.model.chat.Chat;

import java.util.UUID;

public record ChatUpdatedEvent(
        Chat chat
) {
}
