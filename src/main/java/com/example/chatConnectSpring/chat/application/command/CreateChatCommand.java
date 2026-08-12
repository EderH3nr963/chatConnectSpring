package com.example.chatConnectSpring.chat.application.command;

import com.example.chatConnectSpring.chat.infrastructure.adapters.out.persistence.chat.ChatTypeEnum;

import java.util.UUID;

public record CreateChatCommand(
        UUID createdByUserId,
        String title,
        ChatTypeEnum type
) {}
