package com.example.chatConnectSpring.chat.domain.ports.out;

import com.example.chatConnectSpring.chat.domain.model.Message;
import com.example.chatConnectSpring.chat.domain.model.chatParticipant.ChatParticipant;

import java.util.List;
import java.util.UUID;

public interface MessageNotificationPort {
    void notifyMessageSent(Message message, List<ChatParticipant> participantsNonActiveInChat);
    void notifyMessageEdited(Message message);
    void notifyMessageDeleted(UUID chatId, UUID messageId);
}
