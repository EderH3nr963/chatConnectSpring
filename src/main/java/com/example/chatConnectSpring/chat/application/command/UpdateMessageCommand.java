package com.example.chatConnectSpring.chat.application.command;

import java.util.UUID;

public record UpdateMessageCommand(UUID chatId, UUID messageId, UUID userId, String content) {}
