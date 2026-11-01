package com.example.chatConnectSpring.chat.application.commands;

import com.example.chatConnectSpring.chat.application.exceptions.InvalidMessageException;

import java.util.UUID;

public record EditMessageCommand(
        UUID messageId,
        UUID chatId,
        String content
) {
    public EditMessageCommand(UUID messageId, UUID chatId, String content) {
        this.messageId = messageId;
        this.chatId = chatId;
        this.content = content;
        
        validate();
    }
    
    private void validate() {
        if (this.content() == null || this.content().trim().isEmpty()) {
            throw new InvalidMessageException("Message content cannot be empty.");
        }
    }
}
