package com.example.chatConnectSpring.chat.application.command;

import java.util.UUID;

public record SendMessageCommand(UUID chatId, UUID userId, String content) {}
