package com.example.chatConnectSpring.auth.application.services;

import com.example.chatConnectSpring.auth.application.exceptions.InvalidVerificationCodeException;
import com.example.chatConnectSpring.auth.application.responses.LoginResponse;
import com.example.chatConnectSpring.auth.domain.model.TwoFactorVerification;
import com.example.chatConnectSpring.auth.domain.model.vo.Email;
import com.example.chatConnectSpring.auth.domain.model.vo.TwoFactorCode;
import com.example.chatConnectSpring.auth.domain.ports.in.TwoFactorVerificationUseCase;
import com.example.chatConnectSpring.auth.domain.ports.out.CredentialsRepository;
import com.example.chatConnectSpring.auth.domain.ports.out.TwoFactorVerificationRepository;
import com.example.chatConnectSpring.user.domain.model.User;
import com.example.chatConnectSpring.user.domain.port.in.UserUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class TwoFactorVerificationService implements TwoFactorVerificationUseCase {

    private final CredentialsRepository credentialsRepository;
    private final UserUseCase userUseCase;
    private final TwoFactorVerificationRepository verificationRepository;
    private final TokenPairFactory tokenPairFactory;

    public TwoFactorVerificationService(CredentialsRepository credentialsRepository,
                                        UserUseCase userUseCase,
                                        TwoFactorVerificationRepository verificationRepository,
                                        TokenPairFactory tokenPairFactory) {
        this.credentialsRepository = credentialsRepository;
        this.userUseCase = userUseCase;
        this.verificationRepository = verificationRepository;
        this.tokenPairFactory = tokenPairFactory;
    }

    @Transactional
    @Override
    public LoginResponse verify(String rawEmail, String rawCode) {
        Email email = Email.of(rawEmail);
        TwoFactorCode candidate = TwoFactorCode.of(rawCode);
        User user = resolveUser(email);
        TwoFactorVerification verification = loadVerification(user);
        ensureNotExpired(verification);
        ensureCodeMatches(verification, candidate);
        consume(verification);
        return tokenPairFactory.createFor(user);
    }

    private User resolveUser(Email email) {
        var credentials = credentialsRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidVerificationCodeException("Código de verificação expirado ou inválido."));
        User user = userUseCase.findById(credentials.userId());
        if (user == null) {
            throw new InvalidVerificationCodeException("Código de verificação expirado ou inválido.");
        }
        return user;
    }

    private TwoFactorVerification loadVerification(User user) {
        return verificationRepository.findByUserId(user.getId())
                .orElseThrow(() -> new InvalidVerificationCodeException("Código de verificação expirado ou inválido."));
    }

    private void ensureNotExpired(TwoFactorVerification verification) {
        if (verification.isExpired(Instant.now())) {
            verificationRepository.deleteByUserId(verification.userId());
            throw new InvalidVerificationCodeException("Código de verificação expirado.");
        }
    }

    private void ensureCodeMatches(TwoFactorVerification verification, TwoFactorCode candidate) {
        if (!verification.codeMatches(candidate)) {
            throw new InvalidVerificationCodeException("Código de verificação incorreto.");
        }
    }

    private void consume(TwoFactorVerification verification) {
        verificationRepository.deleteByUserId(verification.userId());
    }
}
