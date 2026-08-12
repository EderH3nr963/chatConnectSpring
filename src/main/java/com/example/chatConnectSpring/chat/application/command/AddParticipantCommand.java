package com.example.chatConnectSpring.chat.application.command;

import java.util.UUID;

public record AddParticipantCommand(
        UUID chatId,
        UUID userId
) {}
