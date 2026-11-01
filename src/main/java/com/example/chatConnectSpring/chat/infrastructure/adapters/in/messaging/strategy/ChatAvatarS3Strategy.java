package com.example.chatConnectSpring.chat.infrastructure.adapters.in.messaging.strategy;

import com.example.chatConnectSpring.chat.domain.ports.in.UpdateAvatarChatUseCase;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ChatAvatarS3Strategy implements S3MediaProcessorStrategy {
    
    private static final String CHAT_AVATAR_PREFIX = "uploads/chat/avatar";
    private final UpdateAvatarChatUseCase updateAvatarChatUseCase;
    
    public ChatAvatarS3Strategy(UpdateAvatarChatUseCase updateAvatarChatUseCase) {
        this.updateAvatarChatUseCase = updateAvatarChatUseCase;
    }
    
    @Override
    public boolean supports(String objectKey) {
        return objectKey != null && objectKey.startsWith(CHAT_AVATAR_PREFIX);
    }
    
    @Override
    public void process(String objectKey, JsonNode recordNode) {
        UUID chatId = extractIdFromKey(objectKey);
        updateAvatarChatUseCase.execute(chatId, objectKey);
    }
    
    private UUID extractIdFromKey(String objectKey) {
        String filename = objectKey.replace(CHAT_AVATAR_PREFIX, "");
        if (filename.startsWith("/")) {
            filename = filename.substring(1);
        }
        String chatIdStr = filename.substring(0, 36);
        return UUID.fromString(chatIdStr);
    }
}