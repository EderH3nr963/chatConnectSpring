package com.example.chatConnectSpring.friendship.application.services;

import com.example.chatConnectSpring.friendship.application.events.UserBlockedEvent;
import com.example.chatConnectSpring.friendship.application.events.UserUnblockedEvent;
import com.example.chatConnectSpring.friendship.application.exception.InvalidFriendshipException;
import com.example.chatConnectSpring.friendship.application.exception.UserIsNotBlockedException;
import com.example.chatConnectSpring.friendship.domain.model.userblock.UserBlock;
import com.example.chatConnectSpring.friendship.domain.ports.in.UserBlockUseCase;
import com.example.chatConnectSpring.friendship.domain.ports.out.UserBlockRepository;
import com.example.chatConnectSpring.user.application.service.UserService;
import com.example.chatConnectSpring.user.domain.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class UserBlockService implements UserBlockUseCase {

    private static final Logger log = LoggerFactory.getLogger(UserBlockService.class);
    
    private final UserService userService;
    private final UserBlockRepository userBlockRepository;
    private final ApplicationEventPublisher publisher;
    
    public UserBlockService(
            UserService userService,
            UserBlockRepository userBlockRepository,
            ApplicationEventPublisher publisher
    ) {
        this.userService = userService;
        this.userBlockRepository = userBlockRepository;
        this.publisher = publisher;
    }
    
    @Override
    @Transactional
    public void unblockUser(UUID userId, UUID blockedUserId) {
        if (userId == null || blockedUserId == null) {
            throw new IllegalArgumentException("User IDs cannot be null.");
        }
        
        boolean existing = userBlockRepository.existsByBlockerIdAndBlockedId(userId, blockedUserId);
        if (!existing) {
            log.warn("Unblock failed: {} is not blocked by {}", blockedUserId, userId);
            throw new UserIsNotBlockedException("User is not blocked.");
        }
        
        log.info("Unblock: {} unblocked {}", userId, blockedUserId);
        userBlockRepository.deleteByBlockerAndBlockedUserId(userId, blockedUserId);
        publisher.publishEvent(new UserUnblockedEvent(userId, blockedUserId));
        log.debug("Published UserUnblockedEvent {} -> {}", userId, blockedUserId);
    }
    
    @Override
    @Transactional
    public void blockUser(UUID userId, UUID blockedUserId) {
        if (userId == null || blockedUserId == null) {
            throw new IllegalArgumentException("User IDs cannot be null.");
        }
        if (Objects.equals(userId, blockedUserId)) {
            throw new InvalidFriendshipException("Operation cannot be performed with yourself.");
        }
        
        User user = userService.findById(userId);
        if (user == null) {
            throw new InvalidFriendshipException("User does not exist: " + userId);
        }
        
        User targetUser = userService.findById(blockedUserId);
        if (targetUser == null) {
            throw new InvalidFriendshipException("User does not exist: " + blockedUserId);
        }
        
        boolean existing = userBlockRepository.existsByBlockerIdAndBlockedId(userId, blockedUserId);
        if (existing) {
            log.warn("Block failed: {} already blocked {}", userId, blockedUserId);
            throw new InvalidFriendshipException("User is already blocked.");
        }
        
        UserBlock userBlock = new UserBlock();
        userBlock.setBlockerId(userId);
        userBlock.setBlockedId(blockedUserId);
        
        userBlockRepository.save(userBlock);
        log.info("Block: {} blocked {} blockId={}", userId, blockedUserId, userBlock.getId());
        publisher.publishEvent(new UserBlockedEvent(userId, blockedUserId));
        log.debug("Published UserBlockedEvent {} -> {}", userId, blockedUserId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UserBlock> listBlockedUsers(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null.");
        }
        return userBlockRepository.findBlockedBy(userId);
    }
}
