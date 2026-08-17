package com.example.chatConnectSpring.chat.application.commands;

import java.util.List;

public record CreateGroupChatCommand(
        String title,
        String description,
        List<CreateChatParticipantCommand> participants
) {
}
