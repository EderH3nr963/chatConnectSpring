package com.example.chatConnectSpring.usuario.infrastructure.adapters.in.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AtualizarSenhaRequestDTO(
        @NotBlank String senhaAtual,
        @NotBlank @Size(min = 8, message = "A nova senha deve ter ao menos 8 caracteres") String novaSenha
) {}
