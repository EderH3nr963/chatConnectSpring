package com.example.chatConnectSpring.auth.infrastructure.adapters.in.http.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PreRegisterRequestDTO(
        @Email(message = "E-mail inválido")
        @NotBlank(message = "E-mail é obrigatório")
        String email,

        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
        String password,

        @NotBlank(message = "Username é obrigatório")
        @Size(min = 2, max = 30, message = "Username deve ter entre 2 e 30 caracteres")
        String username
) {}
