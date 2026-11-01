package com.example.chatConnectSpring.auth.application.services;

import com.example.chatConnectSpring.auth.domain.exception.InvalidResetCodeException;
import com.example.chatConnectSpring.auth.domain.model.Credentials;
import com.example.chatConnectSpring.auth.domain.model.PasswordResetRequest;
import com.example.chatConnectSpring.auth.domain.model.vo.Email;
import com.example.chatConnectSpring.auth.domain.model.vo.PasswordHash;
import com.example.chatConnectSpring.auth.domain.model.vo.PasswordHasher;
import com.example.chatConnectSpring.auth.domain.model.vo.RawPassword;
import com.example.chatConnectSpring.auth.domain.model.vo.ResetCode;
import com.example.chatConnectSpring.auth.domain.ports.in.RequestPasswordResetUseCase;
import com.example.chatConnectSpring.auth.domain.ports.in.ResetPasswordUseCase;
import com.example.chatConnectSpring.auth.domain.ports.out.CredentialsRepository;
import com.example.chatConnectSpring.auth.domain.ports.out.MailSender;
import com.example.chatConnectSpring.auth.domain.ports.out.PasswordResetRepository;
import com.example.chatConnectSpring.auth.domain.ports.out.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PasswordResetService implements RequestPasswordResetUseCase, ResetPasswordUseCase {

    private final CredentialsRepository credentialsRepository;
    private final PasswordResetRepository resetRepository;
    private final PasswordHasher passwordHasher;
    private final MailSender mailSender;
    private final RefreshTokenRepository refreshTokenRepository;
    private final int ttlMinutes;

    public PasswordResetService(CredentialsRepository credentialsRepository,
                                PasswordResetRepository resetRepository,
                                PasswordHasher passwordHasher,
                                MailSender mailSender,
                                RefreshTokenRepository refreshTokenRepository,
                                @Value("${auth.password-reset.ttl-minutes:15}") int ttlMinutes) {
        this.credentialsRepository = credentialsRepository;
        this.resetRepository = resetRepository;
        this.passwordHasher = passwordHasher;
        this.mailSender = mailSender;
        this.refreshTokenRepository = refreshTokenRepository;
        this.ttlMinutes = ttlMinutes;
    }

    @Override
    public void request(String rawEmail) {
        Email email = Email.of(rawEmail);

        credentialsRepository.findByEmail(email).ifPresent(ignored -> {
            PasswordResetRequest reset = PasswordResetRequest.create(email, ttlMinutes);
            resetRepository.save(reset);
            mailSender.sendResetCode(email, reset.code());
        });
    }

    @Override
    @Transactional
    public void reset(String rawEmail, String rawCode, String rawPassword) {
        Email email = Email.of(rawEmail);
        ResetCode code = ResetCode.of(rawCode);
        RawPassword newPassword = RawPassword.of(rawPassword);

        PasswordResetRequest reset = resetRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidResetCodeException("Invalid or expired reset code"));

        if (reset.isExpired()) {
            resetRepository.deleteByEmail(email);
            throw new InvalidResetCodeException("Reset code expired");
        }

        if (!reset.hasAttemptsLeft()) {
            resetRepository.deleteByEmail(email);
            throw new InvalidResetCodeException("Too many attempts, request a new code");
        }

        if (!reset.code().matches(code)) {
            reset.incrementAttempts();
            resetRepository.save(reset);
            throw new InvalidResetCodeException("Invalid reset code");
        }

        Credentials credentials = credentialsRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidResetCodeException("Invalid reset code"));

        PasswordHash hash = passwordHasher.hash(newPassword);
        Credentials updatedCredentials = Credentials.reconstitute(
                credentials.id(),
                credentials.userId(),
                credentials.email(),
                hash,
                credentials.loggedAt()
        );
        credentialsRepository.save(updatedCredentials);

        refreshTokenRepository.revokeAllByUserId(credentials.userId());
        resetRepository.deleteByEmail(email);
    }
}
