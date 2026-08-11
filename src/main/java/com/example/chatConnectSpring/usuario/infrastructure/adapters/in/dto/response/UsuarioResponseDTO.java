package com.example.chatConnectSpring.usuario.infrastructure.adapters.in.dto.response;


import java.util.UUID;

public record UsuarioResponseDTO(
        UUID id,
        String email,
        String username
) {
}
