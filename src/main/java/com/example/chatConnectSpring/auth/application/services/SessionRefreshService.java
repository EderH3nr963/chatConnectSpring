package com.example.chatConnectSpring.auth.application.services;

import com.example.chatConnectSpring.auth.application.responses.LoginResponse;
import com.example.chatConnectSpring.auth.domain.exception.InvalidRefreshTokenException;
import com.example.chatConnectSpring.auth.domain.exception.InvalidCredentialsException;
import com.example.chatConnectSpring.auth.domain.model.RefreshToken;
import com.example.chatConnectSpring.auth.domain.model.vo.RawRefreshToken;
import com.example.chatConnectSpring.auth.domain.model.vo.RefreshTokenHash;
import com.example.chatConnectSpring.auth.domain.ports.in.RefreshSessionUseCase;
import com.example.chatConnectSpring.auth.domain.ports.out.RefreshTokenIssuer;
import com.example.chatConnectSpring.auth.domain.ports.out.RefreshTokenRepository;
import com.example.chatConnectSpring.user.domain.model.User;
import com.example.chatConnectSpring.user.domain.port.in.UserUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SessionRefreshService implements RefreshSessionUseCase {

    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenIssuer refreshTokenIssuer;
    private final UserUseCase userUseCase;
    private final TokenPairFactory tokenPairFactory;

    public SessionRefreshService(RefreshTokenRepository refreshTokenRepository,
                                 RefreshTokenIssuer refreshTokenIssuer,
                                 UserUseCase userUseCase,
                                 TokenPairFactory tokenPairFactory) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenIssuer = refreshTokenIssuer;
        this.userUseCase = userUseCase;
        this.tokenPairFactory = tokenPairFactory;
    }

    @Override
    @Transactional
    public LoginResponse refresh(RawRefreshToken rawToken) {
        RefreshToken stored = findActiveTokenOrFail(rawToken);
        User user = findUserOrFail(stored.userId());
        revoke(stored);
        return tokenPairFactory.createFor(user);
    }

    private RefreshToken findActiveTokenOrFail(RawRefreshToken raw) {
        RefreshTokenHash hash = refreshTokenIssuer.hash(raw);
        RefreshToken token = refreshTokenRepository.findByTokenHash(hash)
                .orElseThrow(InvalidRefreshTokenException::new);
        ensureActive(token);
        return token;
    }

    private void ensureActive(RefreshToken token) {
        if (!token.isActive()) {
            throw new InvalidRefreshTokenException();
        }
    }

    private User findUserOrFail(java.util.UUID userId) {
        User user = userUseCase.findById(userId);
        if (user == null) {
            throw new InvalidCredentialsException();
        }
        return user;
    }

    private void revoke(RefreshToken token) {
        refreshTokenRepository.revoke(token.id());
    }
}
