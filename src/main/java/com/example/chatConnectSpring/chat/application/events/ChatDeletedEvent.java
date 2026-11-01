package com.example.chatConnectSpring.chat.application.events;

import java.util.UUID;

public record ChatDeletedEvent(
        UUID chatId
) {
}
