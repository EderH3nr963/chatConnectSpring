package com.example.chatConnectSpring.auth.infrastructure.adapters.out.persistence.auth;

import com.example.chatConnectSpring.auth.domain.model.Credentials;
import com.example.chatConnectSpring.auth.domain.model.vo.Email;
import com.example.chatConnectSpring.auth.domain.ports.out.CredentialsRepository;
import com.example.chatConnectSpring.auth.infrastructure.mappers.CredentialsMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class CredentialsRepositoryAdapter implements CredentialsRepository {

    private final CredentialsJpaRepository repository;

    public CredentialsRepositoryAdapter(CredentialsJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Credentials save(Credentials credentials) {
        return CredentialsMapper.toDomain(repository.save(CredentialsMapper.toEntity(credentials)));
    }

    @Override
    public Optional<Credentials> findByEmail(Email email) {
        return repository.findByEmail(email.value())
                .map(CredentialsMapper::toDomain);
    }

    @Override
    public Optional<Credentials> findByUserId(UUID userId) {
        return repository.findByUserId(userId)
                .map(CredentialsMapper::toDomain);
    }
    
    @Override
    public void updateEmailByUserId(UUID userId, Email email) {
        repository.updateEmailByUserId(userId, email.value());
    }
    
    @Override
    public boolean existsByEmail(Email email) {
        return repository.existsByEmail(email.value());
    }
}
