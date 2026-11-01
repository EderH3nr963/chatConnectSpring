package com.example.chatConnectSpring.friendship.infrastructure.adapters.in.http.dto.response;

import com.example.chatConnectSpring.friendship.domain.model.friendShip.FriendShipStatus;
import com.example.chatConnectSpring.user.infrastructure.adapters.in.http.dto.response.UserSummaryResponseDTO;

import java.time.OffsetDateTime;
import java.util.UUID;

public record FriendshipResponseDTO(
        UUID id,
        UserSummaryResponseDTO user,
        UserSummaryResponseDTO anotherUser,
        FriendShipStatus status,
        OffsetDateTime invitedAt,
        OffsetDateTime acceptedAt,
        OffsetDateTime updatedAt
) {
}
