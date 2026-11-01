package com.example.chatConnectSpring.auth.application.services;

import com.example.chatConnectSpring.auth.application.exceptions.InvalidCurrentPasswordException;
import com.example.chatConnectSpring.auth.application.ports.in.UpdatePasswordUseCase;
import com.example.chatConnectSpring.auth.domain.model.Credentials;
import com.example.chatConnectSpring.auth.domain.model.vo.PasswordHash;
import com.example.chatConnectSpring.auth.domain.model.vo.PasswordHasher;
import com.example.chatConnectSpring.auth.domain.model.vo.RawPassword;
import com.example.chatConnectSpring.auth.domain.ports.out.CredentialsRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UpdatePasswordService implements UpdatePasswordUseCase {

    private final CredentialsRepository credentialsRepository;
    private final PasswordHasher passwordHasher;

    public UpdatePasswordService(CredentialsRepository credentialsRepository, PasswordHasher passwordHasher) {
        this.credentialsRepository = credentialsRepository;
        this.passwordHasher = passwordHasher;
    }

    @Override
    public void updatePassword(UUID userId, String currentPassword, String newPassword) {
        Credentials credentials = credentialsRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("Credentials not found for user"));

        if (!credentials.matches(RawPassword.of(currentPassword), passwordHasher)) {
            throw new InvalidCurrentPasswordException("Senha atual incorreta.");
        }

        PasswordHash newHash = passwordHasher.hash(RawPassword.of(newPassword));
        
        Credentials updated = Credentials.reconstitute(
                credentials.id(),
                credentials.userId(),
                credentials.email(),
                newHash,
                credentials.loggedAt()
        );

        credentialsRepository.save(updated);
    }
}
