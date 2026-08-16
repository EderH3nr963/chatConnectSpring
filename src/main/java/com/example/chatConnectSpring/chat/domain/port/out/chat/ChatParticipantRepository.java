package com.example.chatConnectSpring.chat.domain.port.out.chat;

import com.example.chatConnectSpring.chat.domain.model.ChatParticipant;

public interface ChatParticipantRepository {
    ChatParticipant addParticipant(ChatParticipant participant);
}
