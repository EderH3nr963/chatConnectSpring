package com.example.chatConnectSpring.auth.domain.ports.out;

import com.example.chatConnectSpring.auth.domain.model.PasswordResetRequest;
import com.example.chatConnectSpring.auth.domain.model.vo.Email;

import java.util.Optional;

public interface PasswordResetRepository {

    void save(PasswordResetRequest request);

    Optional<PasswordResetRequest> findByEmail(Email email);

    void deleteByEmail(Email email);
}
