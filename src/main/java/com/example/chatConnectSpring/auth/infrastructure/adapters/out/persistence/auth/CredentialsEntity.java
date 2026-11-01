package com.example.chatConnectSpring.auth.infrastructure.adapters.out.persistence.auth;

import com.example.chatConnectSpring.user.infrastructure.adapters.out.persistence.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "auth_credentials",
        indexes = {
                @Index(
                        name = "idx_auth_user_id",
                        columnList = "user_id",
                        unique = true
                ),
                @Index(
                        name = "idx_auth_email",
                        columnList = "email",
                        unique = true
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CredentialsEntity {
    
    @Id
    private UUID id;
    
    @Column(
            name = "user_id",
            nullable = false,
            unique = true
    )
    private UUID userId;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserEntity user;
    
    @Column(
            name = "email",
            nullable = false,
            unique = true
    )
    private String email;
    
    @Column(
            name = "password_hash",
            nullable = false
    )
    private String passwordHash;
    

    @Column(name = "logged_at")
    private OffsetDateTime loggedAt;
}