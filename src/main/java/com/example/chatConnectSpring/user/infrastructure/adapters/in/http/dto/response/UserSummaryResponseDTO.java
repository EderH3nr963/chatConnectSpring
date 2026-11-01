package com.example.chatConnectSpring.user.infrastructure.adapters.in.http.dto.response;


import com.example.chatConnectSpring.user.domain.model.User;

import java.util.UUID;

public record UserSummaryResponseDTO(
        UUID userId,
        String username,
        String avatarKey
) {
}