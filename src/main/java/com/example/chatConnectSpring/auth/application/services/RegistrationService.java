package com.example.chatConnectSpring.auth.application.services;

import com.example.chatConnectSpring.auth.application.commands.PreRegisterCommand;
import com.example.chatConnectSpring.auth.application.commands.RegisterCommand;
import com.example.chatConnectSpring.auth.application.exceptions.InvalidVerificationCodeException;
import com.example.chatConnectSpring.auth.application.responses.LoginResponse;
import com.example.chatConnectSpring.auth.domain.model.Credentials;
import com.example.chatConnectSpring.auth.domain.model.vo.Email;
import com.example.chatConnectSpring.auth.domain.model.vo.PasswordHasher;
import com.example.chatConnectSpring.auth.domain.model.vo.RawPassword;
import com.example.chatConnectSpring.auth.domain.ports.in.RegisterUseCase;
import com.example.chatConnectSpring.auth.domain.ports.out.CredentialsRepository;
import com.example.chatConnectSpring.auth.domain.ports.out.VerificationPort;
import com.example.chatConnectSpring.shared.service.MailService;
import com.example.chatConnectSpring.user.application.exceptions.UserAlreadyExistsException;
import com.example.chatConnectSpring.user.domain.model.User;
import com.example.chatConnectSpring.user.domain.port.in.UserUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
public class RegistrationService implements RegisterUseCase {

    private static final Logger log = LoggerFactory.getLogger(RegistrationService.class);

    private final CredentialsRepository credentialsRepository;
    private final PasswordHasher passwordHasher;
    private final UserUseCase userUseCase;
    private final TokenPairFactory tokenPairFactory;

    private final VerificationPort verificationPort;
    private final MailService mailService;

    public RegistrationService(CredentialsRepository credentialsRepository,
                               PasswordHasher passwordHasher,
                               UserUseCase userUseCase,
                               TokenPairFactory tokenPairFactory,
                               VerificationPort verificationPort,
                               MailService mailService) {
        this.credentialsRepository = credentialsRepository;
        this.passwordHasher = passwordHasher;
        this.userUseCase = userUseCase;
        this.tokenPairFactory = tokenPairFactory;
        this.verificationPort = verificationPort;
        this.mailService = mailService;
    }

    @Override
    @Transactional
    public void preRegister(PreRegisterCommand command) {
        ensureEmailIsAvailable(Email.of(command.email()));
        
        String code = generate6DigitCode();
        String payload = String.format("%s|%s|%s", command.email(), command.username(), command.password());
        
        verificationPort.save("reg:" + command.email(), code + "|" + payload, Duration.ofMinutes(10));
        mailService.sendVerificationCode(command.email(), code);
    }

    @Override
    @Transactional
    public LoginResponse verifyRegistration(String email, String code) {
        String stored = verificationPort.get("reg:" + email)
                .orElseThrow(() -> new InvalidVerificationCodeException("Código de verificação expirado ou inválido."));
        
        String[] parts = stored.split("\\|", 4);
        if (!parts[0].equals(code)) throw new InvalidVerificationCodeException("Código de verificação incorreto.");

        String username = parts[2];
        String password = parts[3];
        
        RegisterCommand command = new RegisterCommand(email, password, username);
        User user = createUser(command, Email.of(email));
        createCredentials(user, Email.of(email), password);
        
        verificationPort.delete("reg:" + email);
        return tokenPairFactory.createFor(user);
    }

    private String generate6DigitCode() {
        return String.format("%06d", new java.util.Random().nextInt(1000000));
    }

    private void ensureEmailIsAvailable(Email email) {
        if (credentialsRepository.existsByEmail(email)) {
            log.warn("Registration failed: email already exists {}", email.value());
            throw new UserAlreadyExistsException("User with email " + email.value() + " already exists");
        }
    }

    private User createUser(RegisterCommand command, Email email) {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(email.value());
        user.setUsername(command.username());
        user.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        return userUseCase.create(user);
    }

    private void createCredentials(User user, Email email, String rawPassword) {
        RawPassword raw = RawPassword.of(rawPassword);
        Credentials credentials = Credentials.create(user.getId(), email, raw, passwordHasher);
        credentialsRepository.save(credentials);
    }
}
