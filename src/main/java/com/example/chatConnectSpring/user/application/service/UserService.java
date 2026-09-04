package com.example.chatConnectSpring.user.application.service;

import com.example.chatConnectSpring.user.application.exceptions.UserNotFoundException;
import com.example.chatConnectSpring.user.domain.model.User;
import com.example.chatConnectSpring.user.domain.port.in.*;
import com.example.chatConnectSpring.user.domain.port.out.UserNotificationPort;
import com.example.chatConnectSpring.user.domain.port.out.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class UserService implements
        SyncClerkUserUseCase,
        FindByClerkUserIdUseCase,
        FindByIdUseCase,
        DeleteUsuarioUseCase,
        FindAllUsersUseCase,
        FindUserByEmailUseCase,
        UpdateUsuarioUseCase {

    private final UsuarioRepository usuarioRepository;
    private final UserNotificationPort userNotificationPort;

    @Autowired
    public UserService(
            UsuarioRepository usuarioRepository,
            @Autowired(required = false) UserNotificationPort userNotificationPort
    ) {
        this.usuarioRepository = usuarioRepository;
        this.userNotificationPort = userNotificationPort;
    }

    @Override
    @Transactional
    public User syncFromClerk(String clerkUserId, String email, String username) {
        User existing = usuarioRepository.findByClerkUserId(clerkUserId);
        if (existing == null && email != null) {
            existing = usuarioRepository.findByEmail(email);
        }

        if (existing == null) {
            User newUser = new User();
            newUser.setClerkUserId(clerkUserId);
            newUser.setEmail(email);
            newUser.setUsername(username);

            User created = usuarioRepository.create(newUser);
            if (userNotificationPort != null) {
                userNotificationPort.notifyUserCreated(created);
            }
            return created;
        }

        boolean changed = false;
        if (!Objects.equals(existing.getEmail(), email)) {
            existing.setEmail(email);
            changed = true;
        }
        if (!Objects.equals(existing.getUsername(), username)) {
            existing.setUsername(username);
            changed = true;
        }
        if (!Objects.equals(existing.getClerkUserId(), clerkUserId)) {
            existing.setClerkUserId(clerkUserId);
            changed = true;
        }

        if (changed) {
            User updated = usuarioRepository.save(existing);
            if (userNotificationPort != null) {
                userNotificationPort.notifyUserUpdated(updated);
            }
            return updated;
        }

        return existing;
    }

    @Override
    @Transactional(readOnly = true)
    public User findByClerkUserId(String clerkUserId) {
        return usuarioRepository.findByClerkUserId(clerkUserId);
    }

    @Override
    @Transactional(readOnly = true)
    public User findById(UUID id) {
        return usuarioRepository.findById(id);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.delete(id);
            if (userNotificationPort != null) {
                userNotificationPort.notifyUserDeleted(id);
            }
        }
    }

    @Override
    @Transactional
    public void deleteByClerkUserId(String clerkUserId) {
        User user = usuarioRepository.findByClerkUserId(clerkUserId);
        if (user != null) {
            usuarioRepository.delete(user.getId());
            if (userNotificationPort != null) {
                userNotificationPort.notifyUserDeleted(user.getId());
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return usuarioRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public User update(UUID id, String username, String email) {
        User user = usuarioRepository.findById(id);
        if (user == null) {
            throw new UserNotFoundException("User not found with id: " + id);
        }

        boolean changed = false;
        if (username != null && !Objects.equals(user.getUsername(), username)) {
            user.setUsername(username);
            changed = true;
        }
        if (email != null && !Objects.equals(user.getEmail(), email)) {
            user.setEmail(email);
            changed = true;
        }

        if (changed) {
            User updated = usuarioRepository.save(user);
            if (userNotificationPort != null) {
                userNotificationPort.notifyUserUpdated(updated);
            }
            return updated;
        }

        return user;
    }
}
