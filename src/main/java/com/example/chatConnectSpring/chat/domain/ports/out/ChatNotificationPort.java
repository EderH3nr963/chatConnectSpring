package com.example.chatConnectSpring.chat.domain.ports.out;

import com.example.chatConnectSpring.chat.domain.model.chat.Chat;
import com.example.chatConnectSpring.chat.domain.ports.out.projections.ChatSummaryDto;
import com.example.chatConnectSpring.chat.infrastructure.adapters.in.websocket.dto.TypingEventDto;

import java.util.UUID;

public interface ChatNotificationPort {
    void notifyJoinedChat(UUID userId, Chat chat);
    void notifyLeftChat(UUID userId, UUID chatId);
    void notifyChatSummaryUpdated(String participantUserId, ChatSummaryDto summaryEvent);
    void notifyNewMessage(String participantUserId, ChatSummaryDto summaryEvent);
    void notifyChatDeleted(String participantUserId, UUID chatId);

    void notifyTypingStatus(TypingEventDto typingEvent);
}
