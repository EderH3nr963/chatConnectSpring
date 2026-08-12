package com.example.chatConnectSpring.chat.infrastructure.adapters.in.dto.request;

import com.example.chatConnectSpring.chat.infrastructure.adapters.out.persistence.chat.ChatTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateChatRequestDTO(
        @NotNull UUID createdByUserId,
        @NotBlank @Size(max = 120) String title,
        ChatTypeEnum type
) {}
