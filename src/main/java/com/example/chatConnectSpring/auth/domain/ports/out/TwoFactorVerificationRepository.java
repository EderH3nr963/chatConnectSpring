package com.example.chatConnectSpring.auth.domain.ports.out;

import com.example.chatConnectSpring.auth.domain.model.TwoFactorVerification;

import java.util.Optional;
import java.util.UUID;

public interface TwoFactorVerificationRepository {
    void save(TwoFactorVerification verification);
    Optional<TwoFactorVerification> findByUserId(UUID userId);
    void deleteByUserId(UUID userId);
}
