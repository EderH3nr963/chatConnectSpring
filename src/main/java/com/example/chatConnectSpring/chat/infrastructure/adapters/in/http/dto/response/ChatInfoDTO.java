package com.example.chatConnectSpring.chat.infrastructure.adapters.in.http.dto.response;

import java.util.UUID;

public record ChatInfoDTO(
        UUID chatId,
        String name,
        String avatarKey
) {
}
