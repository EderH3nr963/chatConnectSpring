package com.example.chatConnectSpring.user.infrastructure.adapters.in.http.dto.request;

import jakarta.validation.constraints.Size;

public record UpdateUsernameRequestDTO(
        @Size(min = 2, max = 30, message = "Username deve ter entre 2 e 30 caracteres")
        String username
) {}
