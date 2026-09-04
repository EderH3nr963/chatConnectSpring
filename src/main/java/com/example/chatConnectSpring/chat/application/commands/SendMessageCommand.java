package com.example.chatConnectSpring.chat.application.commands;

import java.util.UUID;

public record SendMessageCommand(
        UUID chatId,
        String content
) {
}
