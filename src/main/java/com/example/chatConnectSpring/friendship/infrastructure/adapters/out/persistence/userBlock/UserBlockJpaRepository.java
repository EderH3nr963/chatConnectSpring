package com.example.chatConnectSpring.friendship.infrastructure.adapters.out.persistence.userBlock;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserBlockJpaRepository extends JpaRepository<UserBlockEntity, UUID> {
    
    @Query("""
        SELECT COUNT(ub) > 0
        FROM UserBlockEntity ub
        WHERE ub.blockerId = :blockerUserId AND
            ub.blockedId = :blockedUserId
    """)
    boolean existsBetweenUsers(@Param("blockerUserId") UUID blockerUserId, @Param("blockedUserId") UUID blockedUserId);
    
    List<UserBlockEntity> findByBlockerId(UUID userId);
    
    @Modifying
    @Query("""
        DELETE FROM UserBlockEntity ub
        WHERE ub.blockerId = :blockerId AND ub.blockedId = :blockedId
    """)
    void deleteByBlockerIdAndBlockedUserId(@Param("blockerId") UUID blockerId, @Param("blockedId") UUID blockedId);
}
