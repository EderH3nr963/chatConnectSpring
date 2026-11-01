package com.example.chatConnectSpring.user.application.service;

import com.example.chatConnectSpring.auth.application.exceptions.InvalidVerificationCodeException;
import com.example.chatConnectSpring.auth.domain.model.vo.Email;
import com.example.chatConnectSpring.auth.domain.ports.out.CredentialsRepository;
import com.example.chatConnectSpring.auth.domain.ports.out.VerificationPort;
import com.example.chatConnectSpring.shared.service.MailService;
import com.example.chatConnectSpring.user.domain.model.User;
import com.example.chatConnectSpring.user.domain.port.in.EmailUpdateUseCase;
import com.example.chatConnectSpring.user.domain.port.in.UserUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.UUID;

@Service
public class EmailUpdateService implements EmailUpdateUseCase {

    private final VerificationPort verificationPort;
    private final CredentialsRepository credentialsRepository;
    private final MailService mailService;
    private final UserUseCase userUseCase;

    public EmailUpdateService(VerificationPort verificationPort, MailService mailService, UserUseCase userUseCase, CredentialsRepository credentialsRepository) {
        this.verificationPort = verificationPort;
        this.mailService = mailService;
        this.userUseCase = userUseCase;
        this.credentialsRepository = credentialsRepository;
    }

    public void requestEmailUpdate(UUID userId, String newEmail) {
        String code = String.format("%06d", new java.util.Random().nextInt(1000000));
        verificationPort.save("update-email:" + userId, code + "|" + newEmail, Duration.ofMinutes(10));
        mailService.sendVerificationCode(newEmail, code);
    }

    @Transactional
    public void verifyEmailUpdate(UUID userId, String code) {
        String stored = verificationPort.get("update-email:" + userId)
                .orElseThrow(() -> new InvalidVerificationCodeException("Código de verificação expirado ou inválido."));

        String[] parts = stored.split("\\|", 2);
        if (!parts[0].equals(code)) throw new InvalidVerificationCodeException("Código de verificação incorreto.");
    
        String newEmail = parts[1];
        userUseCase.update(userId, null, newEmail);
        credentialsRepository.updateEmailByUserId(userId, Email.of(newEmail));
        verificationPort.delete("update-email:" + userId);
    }
}
