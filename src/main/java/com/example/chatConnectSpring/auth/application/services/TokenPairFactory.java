package com.example.chatConnectSpring.auth.application.services;

import com.example.chatConnectSpring.auth.application.responses.LoginResponse;
import com.example.chatConnectSpring.auth.domain.model.AccessToken;
import com.example.chatConnectSpring.auth.domain.model.GeneratedRefreshToken;
import com.example.chatConnectSpring.auth.domain.model.RefreshToken;
import com.example.chatConnectSpring.auth.domain.model.vo.RawRefreshToken;
import com.example.chatConnectSpring.auth.domain.model.vo.RefreshTokenHash;
import com.example.chatConnectSpring.auth.domain.ports.out.RefreshTokenRepository;
import com.example.chatConnectSpring.auth.domain.ports.out.RefreshTokenIssuer;
import com.example.chatConnectSpring.auth.domain.ports.out.TokenIssuer;
import com.example.chatConnectSpring.user.domain.model.User;
import com.example.chatConnectSpring.user.domain.port.out.UserStoragePort;
import com.example.chatConnectSpring.user.infrastructure.mapper.UserMapper;
import org.springframework.stereotype.Component;

@Component
public class TokenPairFactory {

    private final TokenIssuer tokenIssuer;
    private final RefreshTokenIssuer refreshTokenIssuer;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserStoragePort userStoragePort;

    public TokenPairFactory(TokenIssuer tokenIssuer,
                            RefreshTokenIssuer refreshTokenIssuer,
                            RefreshTokenRepository refreshTokenRepository,
                            UserStoragePort userStoragePort) {
        this.tokenIssuer = tokenIssuer;
        this.refreshTokenIssuer = refreshTokenIssuer;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userStoragePort = userStoragePort;
    }

    public LoginResponse createFor(User user) {
        AccessToken accessToken = tokenIssuer.issue(user.getId());
        GeneratedRefreshToken generated = refreshTokenIssuer.issue();
        persistRefreshToken(user, generated);
        return new LoginResponse(accessToken, generated, UserMapper.toResponseDTO(user, userStoragePort), false);
    }

    public LoginResponse createChallengeFor(User user) {
        return new LoginResponse(null, null, UserMapper.toResponseDTO(user, userStoragePort), true);
    }

    private void persistRefreshToken(User user, GeneratedRefreshToken generated) {
        RefreshTokenHash hash = refreshTokenIssuer.hash(RawRefreshToken.of(generated.value()));
        RefreshToken refreshToken = RefreshToken.issue(user.getId(), generated, hash);
        refreshTokenRepository.save(refreshToken);
    }
}
