package com.example.chatConnectSpring.chat.application.commands;

import java.util.UUID;

public record UpdateChatCommand(
        String title,
        String description,
        UUID chatId
) {
}
