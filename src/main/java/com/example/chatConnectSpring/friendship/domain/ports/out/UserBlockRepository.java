package com.example.chatConnectSpring.friendship.domain.ports.out;

import com.example.chatConnectSpring.friendship.domain.model.userblock.UserBlock;

import java.util.List;
import java.util.UUID;

public interface UserBlockRepository {
    boolean existsByBlockerIdAndBlockedId(UUID userId, UUID blockedUserId);
    
    UserBlock save(UserBlock userBlock);
    
    List<UserBlock> findBlockedBy(UUID userId);
    
    void deleteByBlockerAndBlockedUserId(UUID userId, UUID blockedUserId);
}
