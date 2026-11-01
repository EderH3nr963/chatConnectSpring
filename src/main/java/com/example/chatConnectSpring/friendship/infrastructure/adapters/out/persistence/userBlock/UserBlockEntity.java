package com.example.chatConnectSpring.friendship.infrastructure.adapters.out.persistence.userBlock;

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
        name = "userblock",
        indexes = {
                @Index(name = "idx_userblock_composite", columnList = "blocker_id, blocked_id"),
                @Index(name = "idx_userblock_blocker_id", columnList = "blocker_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserBlockEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name= "blocker_id", nullable = false)
    private UUID blockerId;
    
    @Column(name= "blocked_id", nullable = false)
    private UUID blockedId;
    
    @Column(name= "created_at", nullable = false)
    private OffsetDateTime createdAt;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="blocked_id", updatable = false, insertable = false)
    private UserEntity blockedUser;
    
    @PrePersist
    private void prePersist() {
        this.createdAt = OffsetDateTime.now();
    }
}
