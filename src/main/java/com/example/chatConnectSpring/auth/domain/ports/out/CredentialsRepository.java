package com.example.chatConnectSpring.auth.domain.ports.out;

import com.example.chatConnectSpring.auth.domain.model.Credentials;
import com.example.chatConnectSpring.auth.domain.model.vo.Email;

import java.util.Optional;
import java.util.UUID;

public interface CredentialsRepository {

    Credentials save(Credentials credentials);

    Optional<Credentials> findByEmail(Email email);

    Optional<Credentials> findByUserId(UUID userId);
    
    void updateEmailByUserId(UUID userId, Email email);

    boolean existsByEmail(Email email);
}
