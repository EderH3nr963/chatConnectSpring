package com.example.chatConnectSpring.chat.application.commands;

import java.util.UUID;

public record UpdateChatCommand(
        String name,
        String description,
        UUID chatId
) {
    public UpdateChatCommand(String name, String description, UUID chatId) {
        this.name = name;
        this.description = description;
        this.chatId = chatId;
        
        validate();
    }
    
    private void validate() {
        if (this.name == null && this.description == null) {
            throw new IllegalArgumentException("name and description cannot be null");
        }
        
        if (this.chatId == null) {
            throw new IllegalArgumentException("chatId cannot be null");
        }
        
    }
}
