package com.example.chatConnectSpring.auth.infrastructure.adapters.in.http.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VerifyEmailRequest(
        @Email(message = "E-mail inválido")
        @NotBlank(message = "E-mail é obrigatório")
        String email,

        @NotBlank(message = "Código é obrigatório")
        @Size(min = 6, max = 6, message = "O código deve ter 6 dígitos")
        String code
) {}
