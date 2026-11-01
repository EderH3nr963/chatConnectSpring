package com.example.chatConnectSpring.auth.application.services;

import com.example.chatConnectSpring.auth.application.ports.in.Resend2FACodeUseCase;
import com.example.chatConnectSpring.auth.domain.model.vo.Email;
import com.example.chatConnectSpring.auth.domain.ports.out.CredentialsRepository;
import com.example.chatConnectSpring.user.domain.model.User;
import com.example.chatConnectSpring.user.domain.port.in.UserUseCase;
import org.springframework.stereotype.Service;

/**
 * OC: Tell Don't Ask, No else.
 * Reenvia o código 2FA delegando para o TwoFactorDispatchService se o usuário existir e tiver 2FA ativo.
 */
@Service
public class Resend2FACodeService implements Resend2FACodeUseCase {

    private final CredentialsRepository credentialsRepository;
    private final UserUseCase userUseCase;
    private final TwoFactorDispatchService twoFactorDispatchService;

    public Resend2FACodeService(CredentialsRepository credentialsRepository,
                                UserUseCase userUseCase,
                                TwoFactorDispatchService twoFactorDispatchService) {
        this.credentialsRepository = credentialsRepository;
        this.userUseCase = userUseCase;
        this.twoFactorDispatchService = twoFactorDispatchService;
    }

    @Override
    public void resend(String rawEmail) {
        Email email = Email.of(rawEmail);
        credentialsRepository.findByEmail(email).ifPresent(credentials -> {
            User user = userUseCase.findById(credentials.userId());
            if (user != null && user.requiresTwoFactorVerification()) {
                twoFactorDispatchService.dispatchFor(user);
            }
        });
    }
}
