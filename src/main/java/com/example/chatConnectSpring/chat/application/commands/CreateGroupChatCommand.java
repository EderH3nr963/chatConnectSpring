package com.example.chatConnectSpring.chat.application.commands;

import com.example.chatConnectSpring.chat.application.exceptions.InvalidChatException;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public record CreateGroupChatCommand(
        String name,
        String description,
        List<UUID> memberIds
) {
    
    public void validate(UUID userId) {
        if (this.memberIds == null || this.memberIds.isEmpty()) {
            throw new InvalidChatException("A group chat must have at least one participant.");
        }
        if (this.memberIds.stream().anyMatch(id -> id == null || id.equals(userId))) {
            throw new InvalidChatException("Participant IDs must be valid and cannot include the chat creator.");
        }
        if (new HashSet<>(this.memberIds).size() != this.memberIds.size()) {
            throw new InvalidChatException("Duplicate participant IDs are not allowed.");
        }
    }
}
