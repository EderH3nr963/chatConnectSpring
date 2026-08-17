package com.example.chatConnectSpring.chat.domain.port.in.chat;

import com.example.chatConnectSpring.chat.application.command.RemoveParticipantCommand;

import java.util.UUID;

public interface RemoveParticipantUseCase {
    void removeParticipant(RemoveParticipantCommand command);
}
