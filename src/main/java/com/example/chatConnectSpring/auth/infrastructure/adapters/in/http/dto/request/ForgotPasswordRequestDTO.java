package com.example.chatConnectSpring.auth.infrastructure.adapters.in.http.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequestDTO(@NotBlank @Email String email) {}
