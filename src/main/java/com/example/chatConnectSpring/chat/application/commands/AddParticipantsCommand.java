package com.example.chatConnectSpring.chat.application.commands;

import com.example.chatConnectSpring.chat.application.exceptions.ParticipantAccessDeniedException;
import com.example.chatConnectSpring.chat.application.exceptions.ParticipantNotFoundException;

import java.util.List;
import java.util.UUID;

public record AddParticipantsCommand(
        List<UUID> users,
        UUID chatId
) {
    public AddParticipantsCommand(List<UUID> users, UUID chatId) {
        this.users = users;
        this.chatId = chatId;
        
        validate();
    }
    
    private void validate() {
        if (this.users() == null || this.users().isEmpty() || this.users().stream().anyMatch(java.util.Objects::isNull))
            throw new ParticipantNotFoundException("Participant IDs must be provided.");
        
        if (new java.util.HashSet<>(this.users()).size() != this.users().size())
            throw new ParticipantAccessDeniedException("Duplicate participant IDs are not allowed.");
    }
}
