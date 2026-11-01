package com.example.chatConnectSpring.auth.application.responses;

import com.example.chatConnectSpring.auth.domain.model.AccessToken;
import com.example.chatConnectSpring.auth.domain.model.GeneratedRefreshToken;
import com.example.chatConnectSpring.user.infrastructure.adapters.in.http.dto.response.UserResponseDTO;

public record LoginResponse(
        AccessToken accessToken,
        GeneratedRefreshToken refreshToken,
        UserResponseDTO user,
        boolean hasVerifyCode
) {
    public LoginResponse(AccessToken accessToken, GeneratedRefreshToken refreshToken, UserResponseDTO user) {
        this(accessToken, refreshToken, user, false);
    }
}