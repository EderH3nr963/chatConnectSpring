package com.example.chatConnectSpring.auth.infrastructure.security;

import com.example.chatConnectSpring.auth.domain.model.vo.PasswordHash;
import com.example.chatConnectSpring.auth.domain.model.vo.PasswordHasher;
import com.example.chatConnectSpring.auth.domain.model.vo.RawPassword;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BCryptPasswordHasher implements PasswordHasher {

    private final PasswordEncoder passwordEncoder;

    public BCryptPasswordHasher(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public PasswordHash hash(RawPassword raw) {
        return PasswordHash.of(passwordEncoder.encode(raw.value()));
    }

    @Override
    public boolean matches(RawPassword raw, PasswordHash hash) {
        return passwordEncoder.matches(raw.value(), hash.value());
    }
}
