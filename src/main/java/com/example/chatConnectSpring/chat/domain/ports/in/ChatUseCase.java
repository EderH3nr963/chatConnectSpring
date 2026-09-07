package com.example.chatConnectSpring.chat.domain.ports.in;

import com.example.chatConnectSpring.chat.application.commands.CreateGroupChatCommand;
import com.example.chatConnectSpring.chat.application.commands.CreatePrivateChatCommand;
import com.example.chatConnectSpring.chat.application.commands.UpdateChatCommand;
import com.example.chatConnectSpring.chat.domain.model.chat.Chat;

import java.util.List;
import java.util.UUID;

public interface ChatUseCase {
    Chat createGroupChat(UUID userId, CreateGroupChatCommand command);
    Chat createPrivateChat(UUID userId, CreatePrivateChatCommand command);
    void delete(UUID userId, UUID chatId);
    Chat findChatById(UUID userId, UUID chatId);
    List<Chat> findChatsByUSerId(UUID userId);
    Chat update(UUID userId, UpdateChatCommand command);
    
}
