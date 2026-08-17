package com.example.chatConnectSpring.chat.domain.ports.in;

import java.util.UUID;

public interface RemoveParticipantUseCase {
    void remove(UUID userId, UUID participantId);
}
