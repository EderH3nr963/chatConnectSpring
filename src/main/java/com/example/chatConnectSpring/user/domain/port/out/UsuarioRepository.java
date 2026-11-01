package com.example.chatConnectSpring.user.domain.port.out;

import com.example.chatConnectSpring.user.domain.model.User;

import java.util.List;
import java.util.UUID;

public interface UsuarioRepository {
    User create(User user);
    User save(User user);
    User findById(UUID id);
    List<User> findByIds(List<UUID> ids);
    User findByEmail(String email);
    List<User> findAll();
    void delete(UUID id);
    boolean existsById(UUID id);
    List<User> searchByUsernameOrEmail(String q, UUID excludeId, int limit);
}
