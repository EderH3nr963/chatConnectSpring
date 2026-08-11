package com.example.chatConnectSpring.auth.infrastructure.adapters.in.dto;

import com.example.chatConnectSpring.usuario.infrastructure.adapters.in.dto.response.UsuarioResponseDTO;

public record AuthResponseDTO(String token, UsuarioResponseDTO usuario) {}
