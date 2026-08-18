package com.example.chatConnectSpring.chat.application.commands;

import java.util.List;
import java.util.UUID;

public record CreateGroupChatCommand(
        String title,
        String description,
        List<UUID> participants
) {
}
