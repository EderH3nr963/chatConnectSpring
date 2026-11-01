package com.example.chatConnectSpring.chat.domain.ports.in;

import com.example.chatConnectSpring.chat.application.commands.AddParticipantsCommand;
import com.example.chatConnectSpring.chat.domain.model.chatParticipant.ChatParticipant;

import java.util.List;
import java.util.UUID;

public interface ChatParticipantUseCase {
    void add(UUID userId, AddParticipantsCommand command);
    void remove(UUID userId, UUID participantId, UUID chatId);
    
    List<ChatParticipant> getParticipants(UUID userId, UUID chatId);
    void toggleMute(UUID userId, UUID chatId);
}
