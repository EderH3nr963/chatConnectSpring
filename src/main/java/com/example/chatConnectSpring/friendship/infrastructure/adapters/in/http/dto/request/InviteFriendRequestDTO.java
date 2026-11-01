package com.example.chatConnectSpring.friendship.infrastructure.adapters.in.http.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record InviteFriendRequestDTO(
        @NotNull(message = "targetUserId is mandatory")
        UUID targetUserId
) {
}
