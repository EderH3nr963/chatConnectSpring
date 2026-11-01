package com.example.chatConnectSpring.friendship.domain.ports.in;

import com.example.chatConnectSpring.friendship.domain.model.userblock.UserBlock;

import java.util.List;
import java.util.UUID;

public interface UserBlockUseCase {
    void blockUser(UUID userId, UUID anotherUserId);
    
    void unblockUser(UUID userId, UUID anotherUserId);
    
    List<UserBlock> listBlockedUsers(UUID userId);
}
