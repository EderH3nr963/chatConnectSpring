package com.example.chatConnectSpring.friendship.domain.model.friendShip;

import com.example.chatConnectSpring.user.domain.model.User;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

public class FriendShip {
    private UUID id;
    private UUID userId;
    private UUID anotherUserId;
    private User user;
    private User anotherUser;
    private FriendShipStatus status;
    private OffsetDateTime invitedAt;
    private OffsetDateTime acceptedAt;
    private OffsetDateTime updatedAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getAnotherUserId() {
        return anotherUserId;
    }

    public void setAnotherUserId(UUID anotherUserId) {
        this.anotherUserId = anotherUserId;
    }

    public FriendShipStatus getStatus() {
        return status;
    }

    public void setStatus(FriendShipStatus status) {
        this.status = status;
    }

    public OffsetDateTime getInvitedAt() {
        return invitedAt;
    }

    public void setInvitedAt(OffsetDateTime invitedAt) {
        this.invitedAt = invitedAt;
    }

    public OffsetDateTime getAcceptedAt() {
        return acceptedAt;
    }

    public void setAcceptedAt(OffsetDateTime acceptedAt) {
        this.acceptedAt = acceptedAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public User getAnotherUser() {
        return anotherUser;
    }
    
    public void setAnotherUser(User anotherUser) {
        this.anotherUser = anotherUser;
    }
    
    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isParticipant(UUID checkedUserId) {
        if (checkedUserId == null) return false;
        return checkedUserId.equals(userId) || checkedUserId.equals(anotherUserId);
    }

    public UUID getOtherUserId(UUID currentUserId) {
        if (Objects.equals(currentUserId, userId)) {
            return anotherUserId;
        } else if (Objects.equals(currentUserId, anotherUserId)) {
            return userId;
        }
        return null;
    }

    public boolean isPending() {
        return FriendShipStatus.PENDING == this.status;
    }

    public boolean isAccepted() {
        return FriendShipStatus.ACCEPTED == this.status;
    }
}
