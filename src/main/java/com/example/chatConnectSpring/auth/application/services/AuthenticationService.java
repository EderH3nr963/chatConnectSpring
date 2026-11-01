package com.example.chatConnectSpring.auth.application.services;

import com.example.chatConnectSpring.auth.application.commands.LoginCommand;
import com.example.chatConnectSpring.auth.application.responses.LoginResponse;
import com.example.chatConnectSpring.auth.domain.exception.InvalidCredentialsException;
import com.example.chatConnectSpring.auth.domain.model.Credentials;
import com.example.chatConnectSpring.auth.application.responses.LoginResponse;
import com.example.chatConnectSpring.auth.domain.model.vo.Email;
import com.example.chatConnectSpring.auth.domain.model.vo.PasswordHasher;
import com.example.chatConnectSpring.auth.domain.model.vo.RawPassword;
import com.example.chatConnectSpring.auth.domain.ports.in.AuthenticateUseCase;
import com.example.chatConnectSpring.auth.domain.ports.out.CredentialsRepository;
import com.example.chatConnectSpring.user.domain.model.User;
import com.example.chatConnectSpring.user.domain.port.in.UserUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class AuthenticationService implements AuthenticateUseCase {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);

    private final CredentialsRepository credentialsRepository;
    private final PasswordHasher passwordHasher;
    private final UserUseCase userUseCase;
    private final TokenPairFactory tokenPairFactory;
    private final TwoFactorDispatchService twoFactorDispatchService;

    public AuthenticationService(CredentialsRepository credentialsRepository,
                               PasswordHasher passwordHasher,
                               UserUseCase userUseCase,
                               TokenPairFactory tokenPairFactory,
                               TwoFactorDispatchService twoFactorDispatchService) {
        this.credentialsRepository = credentialsRepository;
        this.passwordHasher = passwordHasher;
        this.userUseCase = userUseCase;
        this.tokenPairFactory = tokenPairFactory;
        this.twoFactorDispatchService = twoFactorDispatchService;
    }

    @Override
    @Transactional
    public LoginResponse login(LoginCommand command) {
        log.info("Login attempt for email={}", command.email());
        Credentials credentials = findCredentialsOrFail(command.email());
        ensurePasswordMatches(command.password(), credentials);
        User user = findUserOrFail(credentials.userId());
        log.info("Login success userId={} email={}", user.getId(), command.email());
        return route(user);
    }

    private LoginResponse route(User user) {
        if (user.requiresTwoFactorVerification()) {
            twoFactorDispatchService.dispatchFor(user);
            return tokenPairFactory.createChallengeFor(user);
        }
        return tokenPairFactory.createFor(user);
    }

    private Credentials findCredentialsOrFail(String rawEmail) {
        Email email = Email.of(rawEmail);
        return credentialsRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);
    }

    private void ensurePasswordMatches(String rawPassword, Credentials credentials) {
        RawPassword raw = RawPassword.of(rawPassword);
        if (!credentials.matches(raw, passwordHasher)) {
            log.warn("Login failed: invalid password for credentialsId={}", credentials.userId());
            throw new InvalidCredentialsException();
        }
    }

    private User findUserOrFail(java.util.UUID userId) {
        User user = userUseCase.findById(userId);
        if (user == null) {
            throw new InvalidCredentialsException();
        }
        return user;
    }
}
