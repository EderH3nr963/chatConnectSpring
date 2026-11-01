package com.example.chatConnectSpring.chat.infrastructure.adapters.in.http.dto.request;

import jakarta.validation.constraints.NotBlank;

public record GenerateGroupImageUploadUrlRequest(
    @NotBlank(message = "Content-Type é obrigatório")
    String contentType
) { }
