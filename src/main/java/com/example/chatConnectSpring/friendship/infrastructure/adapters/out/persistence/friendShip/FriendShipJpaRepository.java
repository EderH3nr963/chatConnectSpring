package com.example.chatConnectSpring.friendship.infrastructure.adapters.out.persistence.friendShip;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FriendShipJpaRepository extends JpaRepository<FriendShipEntity, UUID> {

    @Query("""
        SELECT f FROM FriendShipEntity f
        WHERE (f.userId = :userId1 AND f.anotherUserId = :userId2)
           OR (f.userId = :userId2 AND f.anotherUserId = :userId1)
    """)
    Optional<FriendShipEntity> findBetweenUsers(@Param("userId1") UUID userId1, @Param("userId2") UUID userId2);

    @Query("""
                SELECT f FROM FriendShipEntity f
                JOIN FETCH f.user
                JOIN FETCH f.anotherUser
                WHERE (f.userId = :userId OR f.anotherUserId = :userId)
                  AND f.status = 'ACCEPTED'
            """)
    List<FriendShipEntity> findAcceptedFriends(@Param("userId") UUID userId);

    @Query("""
        SELECT f FROM FriendShipEntity f
        WHERE f.anotherUserId = :userId
          AND f.status = 'PENDING'
    """)
    List<FriendShipEntity> findPendingReceived(@Param("userId") UUID userId);

    @Query("""
        SELECT f FROM FriendShipEntity f
        WHERE f.userId = :userId
          AND f.status = 'PENDING'
    """)
    List<FriendShipEntity> findPendingSent(@Param("userId") UUID userId);

    @Modifying
    @Query("""
        DELETE FROM FriendShipEntity f
        WHERE (f.userId = :userId1 AND f.anotherUserId = :userId2)
           OR (f.userId = :userId2 AND f.anotherUserId = :userId1)
    """)
    void deleteBetweenUsers(@Param("userId1") UUID userId1, @Param("userId2") UUID userId2);

    @Query("""
        SELECT COUNT(f) > 0 FROM FriendShipEntity f
        WHERE ((f.userId = :userId1 AND f.anotherUserId = :userId2)
           OR (f.userId = :userId2 AND f.anotherUserId = :userId1))
          AND f.status = 'ACCEPTED'
    """)
    boolean areFriends(@Param("userId1") UUID userId1, @Param("userId2") UUID userId2);
}
