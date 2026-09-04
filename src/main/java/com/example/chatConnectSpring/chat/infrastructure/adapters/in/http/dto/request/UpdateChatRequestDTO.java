package com.example.chatConnectSpring.chat.infrastructure.adapters.in.http.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateChatRequestDTO(
        @NotBlank String title,
        @Size(max = 1000) String description
) {
}
