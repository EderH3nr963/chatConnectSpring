package com.example.chatConnectSpring.friendship.infrastructure.adapters.out.persistence.userBlock;

import com.example.chatConnectSpring.friendship.domain.model.userblock.UserBlock;
import com.example.chatConnectSpring.friendship.domain.ports.out.UserBlockRepository;
import com.example.chatConnectSpring.friendship.infrastructure.mapper.UserBlockMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class UserBlockRepositoryAdapter implements UserBlockRepository {
    
    public UserBlockRepositoryAdapter(UserBlockJpaRepository userBlockJpaRepository) {
        this.userBlockJpaRepository = userBlockJpaRepository;
    }
    
    private UserBlockJpaRepository userBlockJpaRepository;
    
    @Override
    public boolean existsByBlockerIdAndBlockedId(UUID userId, UUID blockedUserId) {
        return userBlockJpaRepository.existsBetweenUsers(userId, blockedUserId);
    }
    
    @Override
    public UserBlock save(UserBlock userBlock) {
        UserBlockEntity entity = UserBlockMapper.toEntity(userBlock);

        UserBlockEntity saved = userBlockJpaRepository.save(entity);
        
        return UserBlockMapper.toDomain(saved);
    }
    
    @Override
    public List<UserBlock> findBlockedBy(UUID userId) {
        return userBlockJpaRepository.findByBlockerId(userId)
                .stream()
                .map(UserBlockMapper::toDomain)
                .toList();
    }
    
    @Override
    public void deleteByBlockerAndBlockedUserId(UUID userId, UUID blockedUserId) {
        userBlockJpaRepository.deleteByBlockerIdAndBlockedUserId(userId, blockedUserId);
    }
}
