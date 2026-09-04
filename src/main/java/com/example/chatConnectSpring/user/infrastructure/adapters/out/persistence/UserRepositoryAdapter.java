package com.example.chatConnectSpring.user.infrastructure.adapters.out.persistence;

import com.example.chatConnectSpring.user.domain.model.User;
import com.example.chatConnectSpring.user.domain.port.out.UsuarioRepository;
import com.example.chatConnectSpring.user.infrastructure.mapper.UserMapper;
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
    public User findByClerkUserId(String clerkUserId) {
        if (clerkUserId == null) {
            return null;
        }
        return userJpaRepository.findByClerkUserId(clerkUserId)
                .map(UserMapper::toDomain)
                .orElse(null);
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
    public void deleteByClerkUserId(String clerkUserId) {
        if (clerkUserId != null) {
            userJpaRepository.deleteByClerkUserId(clerkUserId);
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
    public boolean existsByClerkUserId(String clerkUserId) {
        if (clerkUserId == null) {
            return false;
        }
        return userJpaRepository.existsByClerkUserId(clerkUserId);
    }
}
