package com.example.chatConnectSpring.auth.infrastructure.mappers;

import com.example.chatConnectSpring.auth.domain.model.Credentials;
import com.example.chatConnectSpring.auth.domain.model.vo.Email;
import com.example.chatConnectSpring.auth.domain.model.vo.PasswordHash;
import com.example.chatConnectSpring.auth.infrastructure.adapters.out.persistence.auth.CredentialsEntity;

public class CredentialsMapper {

    private CredentialsMapper() {}

    public static CredentialsEntity toEntity(Credentials domain) {
        CredentialsEntity entity = new CredentialsEntity();
        entity.setId(domain.id());
        entity.setUserId(domain.userId());
        entity.setEmail(domain.email().value());
        entity.setPasswordHash(domain.passwordHash().value());
        entity.setLoggedAt(domain.loggedAt());
        return entity;
    }

    public static Credentials toDomain(CredentialsEntity entity) {
        if (entity == null) {
            return null;
        }
        return Credentials.reconstitute(
                entity.getId(),
                entity.getUserId(),
                Email.of(entity.getEmail()),
                PasswordHash.of(entity.getPasswordHash()),
                entity.getLoggedAt()
        );
    }
}
