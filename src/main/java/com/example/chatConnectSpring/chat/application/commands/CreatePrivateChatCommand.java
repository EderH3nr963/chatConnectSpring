package com.example.chatConnectSpring.chat.application.commands;

import java.util.List;

public record CreatePrivateChatCommand(
        AddParticipantsCommand participants
) {
}
