package com.example.chatConnectSpring.chat.domain.ports.in;

import com.example.chatConnectSpring.chat.application.commands.AddParticipantsCommand;

import java.util.UUID;

public interface ChatParticipantUseCase {
    void add(UUID userId, AddParticipantsCommand command);
    void remove(UUID userId, UUID participantId, UUID chatId);
}
