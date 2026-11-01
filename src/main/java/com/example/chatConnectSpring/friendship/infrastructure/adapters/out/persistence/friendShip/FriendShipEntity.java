package com.example.chatConnectSpring.friendship.infrastructure.adapters.out.persistence.friendShip;

import com.example.chatConnectSpring.friendship.domain.model.friendShip.FriendShipStatus;
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
        name = "friendship",
        indexes = {
                @Index(name = "idx_friendship_user_id", columnList = "user_id"),
                @Index(name = "idx_friendship_another_user_id", columnList = "another_user_id"),
                @Index(name = "idx_friendship_status", columnList = "status"),
                @Index(name = "idx_friendship_composite", columnList = "user_id, another_user_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FriendShipEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "another_user_id", nullable = false)
    private UUID anotherUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private FriendShipStatus status;

    @Column(name = "invited_at")
    private OffsetDateTime invitedAt;
    
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @OneToOne(fetch = FetchType.LAZY)
    private UserEntity user;
    
    @JoinColumn(name = "another_user_id", insertable = false, updatable = false)
    @OneToOne(fetch = FetchType.LAZY)
    private UserEntity anotherUser;

    @Column(name = "accepted_at")
    private OffsetDateTime acceptedAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        OffsetDateTime now = OffsetDateTime.now();
        if (invitedAt == null) {
            invitedAt = now;
        }
        updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}
