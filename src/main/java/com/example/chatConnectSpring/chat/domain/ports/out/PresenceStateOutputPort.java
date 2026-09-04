package com.example.chatConnectSpring.chat.domain.ports.out;

import java.util.Set;

public interface PresenceStateOutputPort {
    void setUserActiveInChat(String chatId, String userId);
    Set<String> usersInChat(String chatId);
    void clearUserActiveChat(String chatId, String userId);
}
