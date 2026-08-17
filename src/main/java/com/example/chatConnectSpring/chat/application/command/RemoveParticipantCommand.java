package com.example.chatConnectSpring.chat.application.command;

import java.util.UUID;

public record RemoveParticipantCommand(
        UUID chatId,
        UUID userId,
        UUID participantId
) {
}
