package com.example.chatConnectSpring.chat.infrastructure.adapters.out.redis;

import com.example.chatConnectSpring.chat.domain.ports.out.PresenceStateOutputPort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class PresenceStateOutputAdapter implements PresenceStateOutputPort {
    private final String PRESENCE_KEY_PREFIX = "presence:chat:";
    
    private final RedisTemplate<String, String> redisTemplate;
    
    public PresenceStateOutputAdapter(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    
    @Override
    public void setUserActiveInChat(String chatId, String userId) {
        redisTemplate.opsForSet().add(PRESENCE_KEY_PREFIX + chatId, userId);
    }
    
    @Override
    public Set<String> usersInChat(String chatId) {
        return redisTemplate.opsForSet().members(PRESENCE_KEY_PREFIX + chatId);
    }
    
    @Override
    public void clearUserActiveChat(String chatId, String userId) {
        redisTemplate.opsForSet().remove(PRESENCE_KEY_PREFIX + chatId, userId);
    }
}
