package com.example.chatConnectSpring.chat.domain.ports.out;

import com.example.chatConnectSpring.chat.domain.model.chat.Chat;
import com.example.chatConnectSpring.chat.domain.model.chatParticipant.ChatParticipant;

import java.util.UUID;

public interface ChatNotificationPort {
    void notifyJoinedChat(UUID userId, Chat chat);
    void notifyLeftChat(UUID userId, UUID chatId);
}
