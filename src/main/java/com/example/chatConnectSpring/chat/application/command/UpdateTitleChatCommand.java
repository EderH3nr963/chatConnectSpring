package com.example.chatConnectSpring.chat.application.command;

import java.util.UUID;

public record UpdateTitleChatCommand(
        UUID chatId,
        UUID userId,
        String newTitle
) {
}
