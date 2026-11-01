package com.example.chatConnectSpring.auth.application.services;

import com.example.chatConnectSpring.auth.domain.model.vo.RawRefreshToken;
import com.example.chatConnectSpring.auth.domain.model.vo.RefreshTokenHash;
import com.example.chatConnectSpring.auth.domain.ports.in.LogoutUseCase;
import com.example.chatConnectSpring.auth.domain.ports.out.RefreshTokenIssuer;
import com.example.chatConnectSpring.auth.domain.ports.out.RefreshTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LogoutService implements LogoutUseCase {

    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenIssuer refreshTokenIssuer;

    public LogoutService(RefreshTokenRepository refreshTokenRepository,
                         RefreshTokenIssuer refreshTokenIssuer) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenIssuer = refreshTokenIssuer;
    }

    @Override
    @Transactional
    public void logout(RawRefreshToken rawToken) {
        RefreshTokenHash hash = refreshTokenIssuer.hash(rawToken);
        refreshTokenRepository.revokeByTokenHash(hash);
    }
}
