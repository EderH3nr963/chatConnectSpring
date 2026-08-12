package com.example.chatConnectSpring.chat.domain.port.out;

import com.example.chatConnectSpring.chat.domain.model.ChatParticipant;

public interface ChatParticipantRepository {
    ChatParticipant addParticipant(ChatParticipant participant);
}
