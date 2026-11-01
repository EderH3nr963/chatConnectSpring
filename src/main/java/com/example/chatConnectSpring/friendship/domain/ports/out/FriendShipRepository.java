package com.example.chatConnectSpring.friendship.domain.ports.out;

import com.example.chatConnectSpring.friendship.domain.model.friendShip.FriendShip;

import java.util.List;
import java.util.UUID;

public interface FriendShipRepository {
    FriendShip save(FriendShip friendShip);
    
    FriendShip findById(UUID id);
    
    FriendShip findBetweenUsers(UUID user1, UUID user2);
    
    List<FriendShip> findAcceptedFriends(UUID userId);
    
    List<FriendShip> findPendingReceived(UUID addresseeId);
    
    List<FriendShip> findPendingSent(UUID requesterId);
    
    void delete(UUID friendshipId);
    
    void deleteBetweenUsers(UUID user1, UUID user2);
    
    boolean existsBetweenUsers(UUID user1, UUID user2);
    
    boolean areFriends(UUID user1, UUID user2);
}
