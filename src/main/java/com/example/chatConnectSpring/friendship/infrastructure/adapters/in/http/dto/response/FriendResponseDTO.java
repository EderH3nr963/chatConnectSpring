package com.example.chatConnectSpring.friendship.infrastructure.adapters.in.http.dto.response;

import java.time.OffsetDateTime;
import java.util.UUID;

public record FriendResponseDTO(
        UUID friendshipId,
        UUID friendId,
        String username,
        String avatarKey,
        OffsetDateTime friendsSince
) {
}
