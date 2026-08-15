package com.example.chatConnectSpring.chat.application.command;

import java.util.UUID;

public record DeleteMessageCommand(UUID chatId, UUID messageId, UUID userId) {}
