package com.example.chatConnectSpring.chat.infrastructure.adapters.in.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record UpdateTitleChatRequestDTO(
        UUID userId,
        @NotBlank String newTitle
) {
}
