package com.example.chatConnectSpring.chat.application.commands;

import java.util.List;
import java.util.UUID;

public record AddParticipantsCommand(
        List<UUID> users,
        UUID chatId
) {
}
