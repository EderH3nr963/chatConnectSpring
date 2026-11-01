package com.example.chatConnectSpring.auth.domain.ports.out;

import com.example.chatConnectSpring.auth.domain.model.AccessToken;
import com.example.chatConnectSpring.auth.domain.model.vo.AccessTokenString;

import java.util.UUID;

public interface TokenIssuer {

    AccessToken issue(UUID userId);

    boolean isAccessToken(AccessTokenString token);

    String extractSubject(AccessTokenString token);
}
