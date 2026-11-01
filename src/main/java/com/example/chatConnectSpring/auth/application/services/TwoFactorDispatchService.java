package com.example.chatConnectSpring.auth.application.services;

import com.example.chatConnectSpring.auth.domain.model.TwoFactorVerification;
import com.example.chatConnectSpring.auth.domain.ports.out.TwoFactorVerificationRepository;
import com.example.chatConnectSpring.shared.service.MailService;
import com.example.chatConnectSpring.user.domain.model.User;
import org.springframework.stereotype.Service;

@Service
public class TwoFactorDispatchService {

    private final TwoFactorVerificationRepository repository;
    private final MailService mailService;

    public TwoFactorDispatchService(TwoFactorVerificationRepository repository, MailService mailService) {
        this.repository = repository;
        this.mailService = mailService;
    }

    public void dispatchFor(User user) {
        TwoFactorVerification verification = TwoFactorVerification.create(user.getId());
        store(verification);
        sendTo(user, verification);
    }

    private void store(TwoFactorVerification verification) {
        repository.save(verification);
    }

    private void sendTo(User user, TwoFactorVerification verification) {
        mailService.sendVerificationCode(user.getEmail(), verification.code().value());
    }
}
