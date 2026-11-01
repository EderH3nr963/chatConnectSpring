package com.example.chatConnectSpring.user.infrastructure.mapper;

import com.example.chatConnectSpring.chat.domain.ports.out.StoragePort;
import com.example.chatConnectSpring.user.domain.model.User;
import com.example.chatConnectSpring.user.domain.port.out.UserStoragePort;
import com.example.chatConnectSpring.user.infrastructure.adapters.in.http.dto.response.UserResponseDTO;
import com.example.chatConnectSpring.user.infrastructure.adapters.in.http.dto.response.UserSummaryResponseDTO;
import com.example.chatConnectSpring.user.infrastructure.adapters.out.persistence.UserEntity;

import java.time.Duration;

public class UserMapper {
    
    private UserMapper() {
    }
    
    public static UserEntity toEntity(User user) {
        if (user == null) {
            return null;
        }
        UserEntity entity = new UserEntity();
        entity.setId(user.getId());
        entity.setEmail(user.getEmail());
        entity.setUsername(user.getUsername());
        entity.setAvatarKey(user.getAvatarKey());
        entity.setActive2fa(user.isActive2fa());
        entity.setCreatedAt(user.getCreatedAt());
        entity.setUpdatedAt(user.getUpdatedAt());
        return entity;
    }
    
    public static User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        User user = new User();
        user.setId(entity.getId());
        user.setEmail(entity.getEmail());
        user.setUsername(entity.getUsername());
        user.setAvatarKey(entity.getAvatarKey());
        user.setActive2fa(entity.isActive2fa());
        user.setCreatedAt(entity.getCreatedAt());
        user.setUpdatedAt(entity.getUpdatedAt());
        return user;
    }
    
    public static UserResponseDTO toResponseDTO(User user) {
        return toResponseDTO(user, (UserStoragePort) null);
    }
    
    public static UserResponseDTO toResponseDTO(User user, UserStoragePort storagePort) {
        if (user == null) {
            return null;
        }
        String avatarUrl = resolveAvatarUrl(user.getAvatarKey(), storagePort);
        String avatarKeyForDto = avatarUrl != null ? avatarUrl : user.getAvatarKey();
        return new UserResponseDTO(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                avatarKeyForDto,
                user.isActive2fa(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
    
    public static UserResponseDTO toResponseDTO(User user, StoragePort storagePort) {
        if (user == null) {
            return null;
        }
        String avatarUrl = resolveAvatarUrl(user.getAvatarKey(), storagePort);
        String avatarKeyForDto = avatarUrl != null ? avatarUrl : user.getAvatarKey();
        return new UserResponseDTO(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                avatarKeyForDto,
                user.isActive2fa(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
        
    }
    
    public static UserSummaryResponseDTO toSummaryDTO(User user) {
        return toSummaryDTO(user, (UserStoragePort) null);
    }
    
    public static UserSummaryResponseDTO toSummaryDTO(User user, UserStoragePort storagePort) {
        if (user == null) {
            return null;
        }
        String avatarUrl = resolveAvatarUrl(user.getAvatarKey(), storagePort);
        String avatarKeyForDto = avatarUrl != null ? avatarUrl : user.getAvatarKey();
        return new UserSummaryResponseDTO(
                user.getId(),
                user.getUsername(),
                avatarKeyForDto
        );
    }
    
    public static UserSummaryResponseDTO toSummaryDTO(User user, StoragePort storagePort) {
        if (user == null) {
            return null;
        }
        String avatarUrl = resolveAvatarUrl(user.getAvatarKey(), storagePort);
        String avatarKeyForDto = avatarUrl != null ? avatarUrl : user.getAvatarKey();
        return new UserSummaryResponseDTO(
                user.getId(),
                user.getUsername(),
                avatarKeyForDto
        );
    }
    
    private static String resolveAvatarUrl(String avatarKey, UserStoragePort storagePort) {
        if (storagePort == null || avatarKey == null || avatarKey.isBlank()) return null;
        if (avatarKey.startsWith("http://") || avatarKey.startsWith("https://")) return avatarKey;
        try {
            return storagePort.generatePresignedDownloadUrl(avatarKey, Duration.ofMinutes(60));
        } catch (Exception ignored) {
            return null;
        }
    }
    
    private static String resolveAvatarUrl(String avatarKey, StoragePort storagePort) {
        if (storagePort == null || avatarKey == null || avatarKey.isBlank()) return null;
        if (avatarKey.startsWith("http://") || avatarKey.startsWith("https://")) return avatarKey;
        try {
            return storagePort.generatePresignedDownloadUrl(avatarKey, Duration.ofMinutes(60));
        } catch (Exception ignored) {
            return null;
        }
    }
}
