package com.example.chatConnectSpring.chat.infrastructure.adapters.in.http.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EditMessageRequestDTO(
        @NotBlank(message = "O conteúdo da mensagem não pode estar vazio")
        @Size(max = 4000, message = "A mensagem pode ter no máximo 4000 caracteres")
        String content
) {
}
