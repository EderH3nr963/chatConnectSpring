package com.example.chatConnectSpring.chat.domain.model;

import com.example.chatConnectSpring.chat.infrastructure.adapters.out.persistence.chatParticipant.ParticipantRole;

import java.time.OffsetDateTime;
import java.util.UUID;

public class ChatParticipant {
    private UUID id;
    private UUID chatId;
    private UUID userId;
    private String participantName;
    private ParticipantRole participantRole = ParticipantRole.PARTICIPANT;
    private OffsetDateTime joinedAt;
    private OffsetDateTime lastReadAt;
    private boolean muted;
    private boolean blocked;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getChatId() { return chatId; }
    public void setChatId(UUID chatId) { this.chatId = chatId; }
    public UUID getUserId() { return userId; }
    public String getParticipantName() { return participantName; }
    public void setParticipantName(String participantName) { this.participantName =  participantName; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public ParticipantRole getParticipantRole() { return participantRole; }
    public void setParticipantRole(ParticipantRole participantRole) { this.participantRole = participantRole; }
    public OffsetDateTime getJoinedAt() { return joinedAt; }
    public void setJoinedAt(OffsetDateTime joinedAt) { this.joinedAt = joinedAt; }
    public OffsetDateTime getLastReadAt() { return lastReadAt; }
    public void setLastReadAt(OffsetDateTime lastReadAt) { this.lastReadAt = lastReadAt; }
    public boolean isMuted() { return muted; }
    public void setMuted(boolean muted) { this.muted = muted; }
    public boolean isBlocked() { return blocked; }
    public void setBlocked(boolean blocked) { this.blocked = blocked; }
}
