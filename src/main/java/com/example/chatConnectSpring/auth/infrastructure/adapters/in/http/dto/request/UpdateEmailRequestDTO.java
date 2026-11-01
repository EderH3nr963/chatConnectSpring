package com.example.chatConnectSpring.auth.infrastructure.adapters.in.http.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateEmailRequestDTO(
        @Email(message = "E-mail inválido")
        @NotBlank(message = "E-mail é obrigatório")
        String newEmail
) {}
