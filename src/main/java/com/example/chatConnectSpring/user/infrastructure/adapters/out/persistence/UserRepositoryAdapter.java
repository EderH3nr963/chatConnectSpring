package com.example.chatConnectSpring.user.infrastructure.adapters.out.persistence;

import com.example.chatConnectSpring.user.domain.model.User;
import com.example.chatConnectSpring.user.domain.port.out.UsuarioRepository;
import com.example.chatConnectSpring.user.infrastructure.mapper.UserMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class UserRepositoryAdapter implements UsuarioRepository {

    private final UserJpaRepository userJpaRepository;

    public UserRepositoryAdapter(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public User create(User user) {
        if (user == null) {
            return null;
        }
        UserEntity entity = UserMapper.toEntity(user);
        UserEntity saved = userJpaRepository.save(entity);
        return UserMapper.toDomain(saved);
    }

    @Override
    public User save(User user) {
        if (user == null) {
            return null;
        }
        UserEntity entity = UserMapper.toEntity(user);
        UserEntity saved = userJpaRepository.save(entity);
        return UserMapper.toDomain(saved);
    }

    @Override
    public User findById(UUID id) {
        if (id == null) {
            return null;
        }
        return userJpaRepository.findById(id)
                .map(UserMapper::toDomain)
                .orElse(null);
    }

    @Override
    public List<User> findByIds(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return userJpaRepository.findAllById(ids)
                .stream()
                .map(UserMapper::toDomain)
                .toList();
    }

    @Override
    public User findByEmail(String email) {
        if (email == null) {
            return null;
        }
        return userJpaRepository.findByEmail(email)
                .map(UserMapper::toDomain)
                .orElse(null);
    }

    @Override
    public List<User> findAll() {
        return userJpaRepository.findAll()
                .stream()
                .map(UserMapper::toDomain)
                .toList();
    }

    @Override
    public void delete(UUID id) {
        if (id != null && userJpaRepository.existsById(id)) {
            userJpaRepository.deleteById(id);
        }
    }

    @Override
    public boolean existsById(UUID id) {
        if (id == null) {
            return false;
        }
        return userJpaRepository.existsById(id);
    }

    @Override
    public List<User> searchByUsernameOrEmail(String q, UUID excludeId, int limit) {
        if (q == null || q.isBlank() || excludeId == null) {
            return List.of();
        }
        int safeLimit = Math.max(1, Math.min(limit, 50));
        return userJpaRepository.searchByUsernameOrEmail(q.trim(), excludeId, PageRequest.of(0, safeLimit))
                .stream()
                .map(UserMapper::toDomain)
                .toList();
    }
}
