package com.example.chatConnectSpring.user.infrastructure.adapters.in.http.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @Size(min = 2, max = 30, message = "username deve ter entre 2 e 30 caracteres")
        String username,
        @Email(message = "email inválido")
        String email
) {}
