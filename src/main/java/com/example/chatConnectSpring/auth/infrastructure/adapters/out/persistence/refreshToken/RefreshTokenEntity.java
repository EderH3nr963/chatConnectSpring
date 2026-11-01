package com.example.chatConnectSpring.auth.infrastructure.adapters.out.persistence.refreshToken;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "refresh_tokens",
        indexes = {
                @Index(
                        name = "idx_refresh_token_hash",
                        columnList = "token_hash",
                        unique = true
                ),
                @Index(
                        name = "idx_refresh_token_user_id",
                        columnList = "user_id"
                ),
                @Index(
                        name = "idx_refresh_token_expires_at",
                        columnList = "expires_at"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenEntity {
    
    @Id
    private UUID id;
    
    @Column(
            name = "token_hash",
            nullable = false,
            unique = true
    )
    private String tokenHash;
    
    @Column(
            name = "user_id",
            nullable = false
    )
    private UUID userId;
    
    @Column(
            name = "expires_at",
            nullable = false
    )
    private OffsetDateTime expiresAt;
    
    @Column(
            name = "revoked_at"
    )
    private OffsetDateTime revokedAt;
    
    @Column(
            name = "created_at",
            nullable = false
    )
    private OffsetDateTime createdAt;
}