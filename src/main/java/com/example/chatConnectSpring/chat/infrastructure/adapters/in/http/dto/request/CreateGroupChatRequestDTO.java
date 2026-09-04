package com.example.chatConnectSpring.chat.infrastructure.adapters.in.http.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record CreateGroupChatRequestDTO(
        @NotBlank String title,
        @Size(max = 1000) String description,
        @NotNull @Size(min = 1) List<UUID> participants
) {
}
