package com.example.chatConnectSpring.auth.infrastructure.adapters.in.http.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record Verify2FARequestDTO(
        @NotBlank(message = "E-mail é obrigatório")
        String email,

        @NotBlank(message = "Código é obrigatório")
        @Pattern(regexp = "\\d{6}", message = "Código deve conter 6 dígitos")
        String code
) {}
