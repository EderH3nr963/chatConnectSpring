package com.example.chatConnectSpring.chat.domain.port.in.chat;

import com.example.chatConnectSpring.chat.application.command.AddParticipantCommand;
import com.example.chatConnectSpring.chat.domain.model.ChatParticipant;

public interface AddParticipantUseCase {
    ChatParticipant addParticipant(AddParticipantCommand command);
}
