package com.example.chatConnectSpring.chat.application.commands;

import java.util.UUID;

public record EditMessageCommand(
        UUID messageId,
        UUID chatId,
        String content
) {
}
