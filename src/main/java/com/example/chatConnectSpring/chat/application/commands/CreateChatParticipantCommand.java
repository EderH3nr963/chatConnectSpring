package com.example.chatConnectSpring.chat.application.commands;

import java.util.UUID;

public record CreateChatParticipantCommand(
        UUID participantId,
        UUID chatId
) {
}
