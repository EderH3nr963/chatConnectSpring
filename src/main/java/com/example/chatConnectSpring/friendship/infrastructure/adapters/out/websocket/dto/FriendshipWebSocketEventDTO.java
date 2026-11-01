package com.example.chatConnectSpring.friendship.infrastructure.adapters.out.websocket.dto;

import com.example.chatConnectSpring.friendship.domain.model.friendShip.FriendShip;
import com.example.chatConnectSpring.friendship.infrastructure.adapters.in.http.dto.response.FriendshipResponseDTO;
import com.example.chatConnectSpring.friendship.infrastructure.mapper.FriendShipMapper;

import java.util.UUID;

public record FriendshipWebSocketEventDTO(
        String eventType,
        UUID targetUserId,
        UUID actorUserId,
        FriendshipResponseDTO friendship
) {
    public static FriendshipWebSocketEventDTO requestReceived(UUID targetUserId, FriendShip friendship) {
        return new FriendshipWebSocketEventDTO("FRIEND_REQUEST_RECEIVED", targetUserId, friendship.getUserId(), FriendShipMapper.toDTO(friendship));
    }

    public static FriendshipWebSocketEventDTO requestAccepted(UUID targetUserId, FriendShip friendship) {
        return new FriendshipWebSocketEventDTO("FRIEND_REQUEST_ACCEPTED", targetUserId, friendship.getAnotherUserId(), FriendShipMapper.toDTO(friendship));
    }

    public static FriendshipWebSocketEventDTO removed(UUID targetUserId, UUID removedByUserId) {
        return new FriendshipWebSocketEventDTO("FRIENDSHIP_REMOVED", targetUserId, removedByUserId, null);
    }
}
