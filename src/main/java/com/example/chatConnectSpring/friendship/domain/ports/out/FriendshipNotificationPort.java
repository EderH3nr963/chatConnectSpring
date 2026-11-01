package com.example.chatConnectSpring.friendship.domain.ports.out;

import com.example.chatConnectSpring.friendship.domain.model.friendShip.FriendShip;

import java.util.UUID;

public interface FriendshipNotificationPort {
    void notifyFriendRequestReceived(UUID targetUserId, FriendShip friendShip);
    
    void notifyFriendRequestAccepted(UUID targetUserId, FriendShip friendShip);
    
    void notifyFriendshipRemoved(UUID targetUserId, UUID removedByUserId);
}
