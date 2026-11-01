package com.example.chatConnectSpring.friendship.application.services;

import com.example.chatConnectSpring.friendship.application.exception.FriendshipAccessDeniedException;
import com.example.chatConnectSpring.friendship.application.exception.FriendshipAlreadyExistsException;
import com.example.chatConnectSpring.friendship.application.exception.FriendshipNotFoundException;
import com.example.chatConnectSpring.friendship.application.exception.InvalidFriendshipException;
import com.example.chatConnectSpring.friendship.domain.model.friendShip.FriendShip;
import com.example.chatConnectSpring.friendship.domain.model.friendShip.FriendShipStatus;
import com.example.chatConnectSpring.friendship.domain.model.userblock.UserBlock;
import com.example.chatConnectSpring.friendship.domain.ports.in.FriendShipUseCase;
import com.example.chatConnectSpring.friendship.domain.ports.out.FriendShipRepository;
import com.example.chatConnectSpring.friendship.domain.ports.out.FriendshipNotificationPort;
import com.example.chatConnectSpring.friendship.domain.ports.out.UserBlockRepository;
import com.example.chatConnectSpring.user.application.service.UserService;
import com.example.chatConnectSpring.user.domain.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class FriendShipService implements FriendShipUseCase {

    private static final Logger log = LoggerFactory.getLogger(FriendShipService.class);

    private final FriendShipRepository friendShipRepository;
    private final UserBlockRepository userBlockRepository;
    private final UserService userService;
    private final FriendshipNotificationPort friendshipNotificationPort;

    public FriendShipService(
            FriendShipRepository friendShipRepository,
            UserService userService,
            @Autowired(required = false) FriendshipNotificationPort friendshipNotificationPort,
            UserBlockRepository userBlockRepository
    ) {
        this.friendShipRepository = friendShipRepository;
        this.userService = userService;
        this.friendshipNotificationPort = friendshipNotificationPort;
        this.userBlockRepository = userBlockRepository;
    }

    @Override
    @Transactional
    public FriendShip invite(UUID userId, UUID userIdToBeInvited) {
        log.info("Friend invite: {} -> {}", userId, userIdToBeInvited);
        if (userId == null || userIdToBeInvited == null) {
            throw new InvalidFriendshipException("User IDs cannot be null.");
        }
        if (Objects.equals(userId, userIdToBeInvited)) {
            throw new InvalidFriendshipException("Operation cannot be performed with yourself.");
        }
        
        User user = userService.findById(userId);
        if (user == null) {
            throw new InvalidFriendshipException("User does not exist: " + userId);
        }
        
        User targetUser = userService.findById(userIdToBeInvited);
        if (targetUser == null) {
            throw new InvalidFriendshipException("User does not exist: " + userIdToBeInvited);
        }
        
        FriendShip existing = friendShipRepository.findBetweenUsers(userId, userIdToBeInvited);
        if (existing != null) {

            if (existing.getStatus() == FriendShipStatus.ACCEPTED) {
                throw new FriendshipAlreadyExistsException("You are already friends with this user.");
            }

            if (existing.getStatus() == FriendShipStatus.PENDING) {
                if (Objects.equals(existing.getUserId(), userId)) {
                    throw new FriendshipAlreadyExistsException("Friend request is already pending.");
                } else {
                    throw new FriendshipAlreadyExistsException("This user has already sent you a friend request.");
                }
            }

            existing.setUserId(userId);
            existing.setAnotherUserId(userIdToBeInvited);
            existing.setStatus(FriendShipStatus.PENDING);
            existing.setInvitedAt(OffsetDateTime.now());
            existing.setAcceptedAt(null);
            existing.setUpdatedAt(OffsetDateTime.now());

            FriendShip saved = friendShipRepository.save(existing);
            enrichWithUsers(saved, user, targetUser);
            notifyRequestReceived(userIdToBeInvited, saved);
            return saved;
        }

        FriendShip newFriendShip = new FriendShip();
        newFriendShip.setUserId(userId);
        newFriendShip.setAnotherUserId(userIdToBeInvited);
        newFriendShip.setStatus(FriendShipStatus.PENDING);
        newFriendShip.setInvitedAt(OffsetDateTime.now());
        newFriendShip.setUpdatedAt(OffsetDateTime.now());

        FriendShip saved = friendShipRepository.save(newFriendShip);
        enrichWithUsers(saved, user, targetUser);
        notifyRequestReceived(userIdToBeInvited, saved);
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public FriendShip findFriendShip(UUID userId, UUID anotherUserId) {
        if (userId == null || anotherUserId == null) {
            throw new InvalidFriendshipException("User IDs cannot be null.");
        }
        
        FriendShip friendShip = friendShipRepository.findBetweenUsers(userId, anotherUserId);
        
        if (friendShip == null) {
            throw new FriendshipNotFoundException("Friendship not found between users.");
        }
        
        return friendShip;
    }
    
    @Override
    @Transactional
    public FriendShip acceptInvite(UUID userId, UUID requesterUserId) {
        log.info("Friend accept: {} accepts from {}", userId, requesterUserId);
        if (userId == null || requesterUserId == null) {
            throw new InvalidFriendshipException("User IDs cannot be null.");
        }
        
        FriendShip friendship = friendShipRepository.findBetweenUsers(userId, requesterUserId);
        if (friendship == null) {
            throw new FriendshipNotFoundException("Friend request not found.");
        }

        if (friendship.getStatus() != FriendShipStatus.PENDING) {
            throw new InvalidFriendshipException("Friend request is not pending. Status: " + friendship.getStatus());
        }

        if (!Objects.equals(friendship.getAnotherUserId(), userId)) {
            throw new FriendshipAccessDeniedException("Only the recipient of the friend request can accept it.");
        }

        friendship.setStatus(FriendShipStatus.ACCEPTED);
        friendship.setAcceptedAt(OffsetDateTime.now());
        friendship.setUpdatedAt(OffsetDateTime.now());

        FriendShip saved = friendShipRepository.save(friendship);
        enrichWithUsers(saved, null, null);
        notifyRequestAccepted(requesterUserId, saved);
        return saved;
    }

    @Override
    @Transactional
    public void rejectInvite(UUID userId, UUID requesterUserId) {
        if (userId == null || requesterUserId == null) {
            throw new InvalidFriendshipException("User IDs cannot be null.");
        }
        
        FriendShip friendship = friendShipRepository.findBetweenUsers(userId, requesterUserId);
        if (friendship == null) {
            throw new FriendshipNotFoundException("Friend request not found.");
        }
        
        if (friendship.getStatus() != FriendShipStatus.PENDING) {
            throw new InvalidFriendshipException("Friend request is not pending.");
        }

        if (!Objects.equals(friendship.getAnotherUserId(), userId)) {
            throw new FriendshipAccessDeniedException("Only the recipient of the friend request can reject it.");
        }

        friendship.setStatus(FriendShipStatus.REJECTED);
        friendship.setUpdatedAt(OffsetDateTime.now());
        friendShipRepository.save(friendship);
    }

    @Override
    @Transactional
    public void cancelInvite(UUID userId, UUID targetUserId) {
        if (userId == null || targetUserId == null) {
            throw new InvalidFriendshipException("User IDs cannot be null.");
        }
        
        FriendShip friendship = friendShipRepository.findBetweenUsers(userId, targetUserId);
        if (friendship == null) {
            throw new FriendshipNotFoundException("Friend request not found.");
        }

        if (friendship.getStatus() != FriendShipStatus.PENDING) {
            throw new InvalidFriendshipException("Friend request is not pending.");
        }

        if (!Objects.equals(friendship.getUserId(), userId)) {
            throw new FriendshipAccessDeniedException("Only the sender of the friend request can cancel it.");
        }

        friendShipRepository.delete(friendship.getId());
    }

    @Override
    @Transactional
    public void removeFriend(UUID userId, UUID friendId) {
        if (userId == null || friendId == null) {
            throw new InvalidFriendshipException("User IDs cannot be null.");
        }
        
        FriendShip friendship = friendShipRepository.findBetweenUsers(userId, friendId);
        if (friendship == null) {
            throw new FriendshipNotFoundException("Friendship not found.");
        }

        if (friendship.getStatus() != FriendShipStatus.ACCEPTED) {
            throw new InvalidFriendshipException("Users are not friends.");
        }

        friendShipRepository.delete(friendship.getId());
        notifyFriendshipRemoved(friendId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FriendShip> listFriends(UUID userId) {
        if (userId == null) {
            throw new InvalidFriendshipException("User ID cannot be null.");
        }
        return friendShipRepository.findAcceptedFriends(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FriendShip> listPendingReceivedInvites(UUID userId) {
        if (userId == null) {
            throw new InvalidFriendshipException("User ID cannot be null.");
        }
        return friendShipRepository.findPendingReceived(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FriendShip> listPendingSentInvites(UUID userId) {
        if (userId == null) {
            throw new InvalidFriendshipException("User ID cannot be null.");
        }
        return friendShipRepository.findPendingSent(userId);
    }

    private void notifyRequestReceived(UUID targetUserId, FriendShip friendShip) {
        if (friendshipNotificationPort != null) {
            friendshipNotificationPort.notifyFriendRequestReceived(targetUserId, friendShip);
        }
    }

    private void notifyRequestAccepted(UUID targetUserId, FriendShip friendShip) {
        if (friendshipNotificationPort != null) {
            friendshipNotificationPort.notifyFriendRequestAccepted(targetUserId, friendShip);
        }
    }

    private void notifyFriendshipRemoved(UUID targetUserId, UUID removedByUserId) {
        if (friendshipNotificationPort != null) {
            friendshipNotificationPort.notifyFriendshipRemoved(targetUserId, removedByUserId);
        }
    }

    private void enrichWithUsers(FriendShip friendship, User user, User targetUser) {
        if (friendship == null) return;
        
        friendship.setUser(user);
        friendship.setAnotherUser(targetUser);
        
        try {
            if (friendship.getUser() == null && friendship.getUserId() != null) {
                friendship.setUser(userService.findById(friendship.getUserId()));
            }
            if (friendship.getAnotherUser() == null && friendship.getAnotherUserId() != null) {
                friendship.setAnotherUser(userService.findById(friendship.getAnotherUserId()));
            }
        } catch (Exception ignored) {
        }
    }
}
