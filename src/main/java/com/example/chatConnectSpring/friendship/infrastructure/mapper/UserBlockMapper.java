package com.example.chatConnectSpring.friendship.infrastructure.mapper;

import com.example.chatConnectSpring.friendship.domain.model.userblock.UserBlock;
import com.example.chatConnectSpring.friendship.infrastructure.adapters.in.http.dto.response.UserBlockDTO;
import com.example.chatConnectSpring.friendship.infrastructure.adapters.out.persistence.userBlock.UserBlockEntity;

public class UserBlockMapper {
    public static UserBlockEntity toEntity(UserBlock userBlock) {
        UserBlockEntity userBlockEntity = new UserBlockEntity();
        userBlockEntity.setId(userBlock.getId());
        userBlockEntity.setBlockedId(userBlock.getBlockedId());
        userBlockEntity.setBlockerId(userBlock.getBlockerId());
        userBlockEntity.setCreatedAt(userBlock.getCreatedAt());
        
        return userBlockEntity;
    }
    
    public static UserBlock toDomain(UserBlockEntity userBlockEntity) {
        UserBlock userBlock = new UserBlock();
        userBlock.setId(userBlockEntity.getId());
        userBlock.setBlockedId(userBlockEntity.getBlockedId());
        userBlock.setBlockerId(userBlockEntity.getBlockerId());
        userBlock.setBlockedUsername(userBlockEntity.getBlockedUser() != null ? userBlockEntity.getBlockedUser().getUsername() : null);
        
        userBlock.setCreatedAt(userBlockEntity.getCreatedAt());
        
        return userBlock;
    }
    
    public static UserBlockDTO toDTO(UserBlock userBlock) {
        UserBlockDTO dto = new UserBlockDTO(userBlock.getBlockerId(), userBlock.getBlockedUsername(), userBlock.getCreatedAt());
        
        return dto;
    }
}
