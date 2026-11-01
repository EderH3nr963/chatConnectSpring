package com.example.chatConnectSpring.user.infrastructure.adapters.in.http.dto.request;

import jakarta.validation.constraints.NotBlank;

public record GenerateUserAvatarUploadUrlRequest(
        @NotBlank(message = "Content-Type é obrigatório")
        String contentType
) {}
