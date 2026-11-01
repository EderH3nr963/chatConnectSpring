package com.example.chatConnectSpring.chat.domain.ports.out;

import com.example.chatConnectSpring.chat.domain.model.chat.Chat;
import com.example.chatConnectSpring.chat.domain.ports.out.projections.ChatSummaryDto;

import java.util.List;
import java.util.UUID;

public interface ChatRepository {
    Chat save(Chat chat);

    Chat update(Chat chat);

    void blockPrivateChat(UUID chatId);
    void unblockPrivateChat(UUID chatId);
    
    boolean canSendMessageInPrivateChat(UUID senderId, UUID targetId);
    
    Chat findPrivateChat(UUID senderId, UUID targetId);

    Chat findById(UUID chatId);
    
    List<ChatSummaryDto> findChatSummaries(UUID userId);

    void delete(UUID chatId);
}
