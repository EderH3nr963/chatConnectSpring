package com.example.chatConnectSpring.auth.domain.model;

public record TokenPair(
        AccessToken accessToken,
        GeneratedRefreshToken refreshToken
) {
}
