package com.example.chatConnectSpring.friendship.infrastructure.mapper;

import com.example.chatConnectSpring.friendship.domain.model.friendShip.FriendShip;
import com.example.chatConnectSpring.friendship.infrastructure.adapters.in.http.dto.response.FriendResponseDTO;
import com.example.chatConnectSpring.friendship.infrastructure.adapters.in.http.dto.response.FriendshipResponseDTO;
import com.example.chatConnectSpring.friendship.infrastructure.adapters.out.persistence.friendShip.FriendShipEntity;
import com.example.chatConnectSpring.user.domain.model.User;
import com.example.chatConnectSpring.user.domain.port.out.UserStoragePort;
import com.example.chatConnectSpring.user.infrastructure.mapper.UserMapper;

public class FriendShipMapper {

    private FriendShipMapper() {}

    public static FriendShipEntity toEntity(FriendShip domain) {
        if (domain == null) {
            return null;
        }
        FriendShipEntity entity = new FriendShipEntity();
        entity.setId(domain.getId());
        entity.setUserId(domain.getUserId());
        entity.setAnotherUserId(domain.getAnotherUserId());
        entity.setStatus(domain.getStatus());
        entity.setInvitedAt(domain.getInvitedAt());
        entity.setAcceptedAt(domain.getAcceptedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    public static FriendShip toDomain(FriendShipEntity entity) {
        if (entity == null) {
            return null;
        }
        FriendShip friendShip = new FriendShip();
        friendShip.setId(entity.getId());
        friendShip.setUserId(entity.getUserId());
        friendShip.setAnotherUserId(entity.getAnotherUserId());
        friendShip.setAnotherUser(UserMapper.toDomain(entity.getAnotherUser()));
        friendShip.setUser(UserMapper.toDomain(entity.getUser()));
        friendShip.setStatus(entity.getStatus());
        friendShip.setInvitedAt(entity.getInvitedAt());
        friendShip.setAcceptedAt(entity.getAcceptedAt());
        friendShip.setUpdatedAt(entity.getUpdatedAt());
        return friendShip;
    }

    public static FriendshipResponseDTO toDTO(FriendShip domain) {
        return toDTO(domain, null);
    }

    public static FriendshipResponseDTO toDTO(FriendShip domain, UserStoragePort storagePort) {
        if (domain == null) {
            return null;
        }
        return new FriendshipResponseDTO(
                domain.getId(),
                UserMapper.toSummaryDTO(domain.getUser(), storagePort),
                UserMapper.toSummaryDTO(domain.getAnotherUser(), storagePort),
                domain.getStatus(),
                domain.getInvitedAt(),
                domain.getAcceptedAt(),
                domain.getUpdatedAt()
        );
    }

    public static FriendResponseDTO toFriendDTO(FriendShip domain, User friendUser) {
        return toFriendDTO(domain, friendUser, null);
    }

    public static FriendResponseDTO toFriendDTO(FriendShip domain, User friendUser, UserStoragePort storagePort) {
        if (domain == null || friendUser == null) {
            return null;
        }
        String avatarKey = friendUser.getAvatarKey();
        String resolvedAvatar = avatarKey;
        if (storagePort != null && avatarKey != null && !avatarKey.isBlank() && !avatarKey.startsWith("http")) {
            try {
                String url = storagePort.generatePresignedDownloadUrl(avatarKey, java.time.Duration.ofMinutes(60));
                if (url != null) resolvedAvatar = url;
            } catch (Exception ignored) {}
        }
        return new FriendResponseDTO(
                domain.getId(),
                friendUser.getId(),
                friendUser.getUsername(),
                resolvedAvatar,
                domain.getAcceptedAt() != null ? domain.getAcceptedAt() : domain.getUpdatedAt()
        );
    }
}
