package com.example.chatConnectSpring.chat.domain.ports.in;

import com.example.chatConnectSpring.chat.domain.model.chatParticipant.ChatParticipant;

import java.util.UUID;

public interface FindParticipantByUserIdAndChatId {
    ChatParticipant find(UUID userId, UUID chatId);
}
