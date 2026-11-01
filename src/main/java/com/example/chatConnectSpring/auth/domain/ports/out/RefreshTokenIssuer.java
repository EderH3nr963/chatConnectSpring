package com.example.chatConnectSpring.auth.domain.ports.out;

import com.example.chatConnectSpring.auth.domain.model.GeneratedRefreshToken;
import com.example.chatConnectSpring.auth.domain.model.vo.RawRefreshToken;
import com.example.chatConnectSpring.auth.domain.model.vo.RefreshTokenHash;

public interface RefreshTokenIssuer {

    GeneratedRefreshToken issue();

    RefreshTokenHash hash(RawRefreshToken raw);
}
