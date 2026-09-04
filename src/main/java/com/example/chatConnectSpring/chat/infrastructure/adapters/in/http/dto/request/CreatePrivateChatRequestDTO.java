package com.example.chatConnectSpring.chat.infrastructure.adapters.in.http.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record CreatePrivateChatRequestDTO(
        @NotNull @Size(min = 1, max = 1) List<UUID> users
) {
}
