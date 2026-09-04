package com.example.chatConnectSpring.user.domain.port.out;

import com.example.chatConnectSpring.user.domain.model.User;

import java.util.List;
import java.util.UUID;

public interface UsuarioRepository {
    User create(User user);
    User save(User user);
    User findById(UUID id);
    User findByClerkUserId(String clerkUserId);
    User findByEmail(String email);
    List<User> findAll();
    void delete(UUID id);
    void deleteByClerkUserId(String clerkUserId);
    boolean existsById(UUID id);
    boolean existsByClerkUserId(String clerkUserId);
}
