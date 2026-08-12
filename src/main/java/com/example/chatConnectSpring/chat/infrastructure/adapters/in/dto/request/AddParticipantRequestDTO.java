package com.example.chatConnectSpring.chat.infrastructure.adapters.in.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddParticipantRequestDTO(
        @NotNull UUID userId
) {}
