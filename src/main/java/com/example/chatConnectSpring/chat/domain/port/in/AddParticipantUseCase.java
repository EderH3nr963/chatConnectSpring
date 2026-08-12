package com.example.chatConnectSpring.chat.domain.port.in;

import com.example.chatConnectSpring.chat.domain.model.ChatParticipant;

import java.util.UUID;

public interface AddParticipantUseCase {
    ChatParticipant addParticipant(UUID chatId, UUID userId);
}
