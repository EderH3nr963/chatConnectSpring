package com.example.chatConnectSpring.auth.domain.model.vo;

public interface PasswordHasher {

    PasswordHash hash(RawPassword raw);

    boolean matches(RawPassword raw, PasswordHash hash);
}
