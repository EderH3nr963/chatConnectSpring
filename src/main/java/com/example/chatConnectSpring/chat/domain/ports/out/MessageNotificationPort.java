package com.example.chatConnectSpring.chat.domain.ports.out;

import com.example.chatConnectSpring.chat.domain.model.message.Message;
import com.example.chatConnectSpring.chat.domain.model.chatParticipant.ChatParticipant;
import com.example.chatConnectSpring.chat.domain.model.message.SendMessageResult;

import java.util.List;
import java.util.UUID;

public interface MessageNotificationPort {
    void notifyMessageSent(Message message);
    void notifyMessageEdited(Message message);
    void notifyMessageDeleted(UUID chatId, UUID messageId);
}
