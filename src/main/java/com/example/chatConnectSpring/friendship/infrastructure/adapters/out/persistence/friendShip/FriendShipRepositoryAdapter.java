package com.example.chatConnectSpring.friendship.infrastructure.adapters.out.persistence.friendShip;

import com.example.chatConnectSpring.friendship.domain.model.friendShip.FriendShip;
import com.example.chatConnectSpring.friendship.domain.ports.out.FriendShipRepository;
import com.example.chatConnectSpring.friendship.infrastructure.mapper.FriendShipMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class FriendShipRepositoryAdapter implements FriendShipRepository {

    private final FriendShipJpaRepository friendShipJpaRepository;

    public FriendShipRepositoryAdapter(FriendShipJpaRepository friendShipJpaRepository) {
        this.friendShipJpaRepository = friendShipJpaRepository;
    }

    @Override
    public FriendShip save(FriendShip friendShip) {
        if (friendShip == null) {
            return null;
        }
        FriendShipEntity entity = FriendShipMapper.toEntity(friendShip);
        FriendShipEntity saved = friendShipJpaRepository.save(entity);
        return FriendShipMapper.toDomain(saved);
    }

    @Override
    public FriendShip findById(UUID id) {
        if (id == null) {
            return null;
        }
        return friendShipJpaRepository.findById(id).map(FriendShipMapper::toDomain).orElse(null);
    }

    @Override
    public FriendShip findBetweenUsers(UUID user1, UUID user2) {
        if (user1 == null || user2 == null) {
            return null;
        }
        return friendShipJpaRepository.findBetweenUsers(user1, user2).map(FriendShipMapper::toDomain).orElse(null);
    }

    @Override
    public List<FriendShip> findAcceptedFriends(UUID userId) {
        if (userId == null) {
            return List.of();
        }
        return friendShipJpaRepository.findAcceptedFriends(userId)
                .stream()
                .map(FriendShipMapper::toDomain)
                .toList();
    }

    @Override
    public List<FriendShip> findPendingReceived(UUID addresseeId) {
        if (addresseeId == null) {
            return List.of();
        }
        return friendShipJpaRepository.findPendingReceived(addresseeId)
                .stream()
                .map(FriendShipMapper::toDomain)
                .toList();
    }

    @Override
    public List<FriendShip> findPendingSent(UUID requesterId) {
        if (requesterId == null) {
            return List.of();
        }
        return friendShipJpaRepository.findPendingSent(requesterId)
                .stream()
                .map(FriendShipMapper::toDomain)
                .toList();
    }

    @Override
    public void delete(UUID friendshipId) {
        if (friendshipId != null) {
            friendShipJpaRepository.deleteById(friendshipId);
        }
    }

    @Override
    public void deleteBetweenUsers(UUID user1, UUID user2) {
        if (user1 != null && user2 != null) {
            friendShipJpaRepository.deleteBetweenUsers(user1, user2);
        }
    }

    @Override
    public boolean existsBetweenUsers(UUID user1, UUID user2) {
        if (user1 == null || user2 == null) {
            return false;
        }
        return friendShipJpaRepository.findBetweenUsers(user1, user2).isPresent();
    }

    @Override
    public boolean areFriends(UUID user1, UUID user2) {
        if (user1 == null || user2 == null) {
            return false;
        }
        return friendShipJpaRepository.areFriends(user1, user2);
    }
}
