package com.example.chatConnectSpring.auth.infrastructure.adapters.in.http.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequestDTO(@NotBlank String refreshToken) { }
