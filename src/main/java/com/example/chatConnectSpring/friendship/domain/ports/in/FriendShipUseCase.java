package com.example.chatConnectSpring.friendship.domain.ports.in;

import com.example.chatConnectSpring.friendship.domain.model.friendShip.FriendShip;

import java.util.List;
import java.util.UUID;

public interface FriendShipUseCase {
    FriendShip invite(UUID userId, UUID userIdToBeInvited);
    
    FriendShip findFriendShip(UUID userId, UUID anotherUserId);
    
    FriendShip acceptInvite(UUID userId, UUID requesterUserId);
    
    void rejectInvite(UUID userId, UUID requesterUserId);
    
    void cancelInvite(UUID userId, UUID targetUserId);
    
    void removeFriend(UUID userId, UUID friendId);
    
    List<FriendShip> listFriends(UUID userId);
    
    List<FriendShip> listPendingReceivedInvites(UUID userId);
    
    List<FriendShip> listPendingSentInvites(UUID userId);
}
 