package com.example.chatConnectSpring.chat.domain.ports.in;

import com.example.chatConnectSpring.chat.application.commands.CreateGroupChatCommand;
import com.example.chatConnectSpring.chat.application.commands.GenerateGroupImageUploadUrlCommand;
import com.example.chatConnectSpring.chat.application.commands.UpdateChatCommand;
import com.example.chatConnectSpring.chat.domain.model.chat.Chat;
import com.example.chatConnectSpring.chat.domain.ports.out.projections.ChatSummaryDto;

import java.util.List;
import java.util.UUID;

public interface ChatUseCase {
    Chat createGroupChat(UUID userId, CreateGroupChatCommand command);
    Chat createPrivateChat(UUID userId, UUID anotherUserId);
    void delete(UUID userId, UUID chatId);
    Chat findChatById(UUID userId, UUID chatId);
    List<ChatSummaryDto> findChatsSummaryByUserId(UUID userId);
    Chat update(UUID userId, UpdateChatCommand command);
    String generateGroupImageUploadUrl(UUID userId, UUID chatId, GenerateGroupImageUploadUrlCommand command);
}
