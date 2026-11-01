package com.example.chatConnectSpring.auth.domain.ports.in;

import com.example.chatConnectSpring.auth.domain.model.vo.RawRefreshToken;

public interface LogoutUseCase {

    void logout(RawRefreshToken rawToken);
}
