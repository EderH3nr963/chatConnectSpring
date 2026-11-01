package com.example.chatConnectSpring.user.application.service;

import com.example.chatConnectSpring.shared.utils.GetExtensionFromContentType;
import com.example.chatConnectSpring.user.application.commands.GenerateUserAvatarUploadUrlCommand;
import com.example.chatConnectSpring.user.application.exceptions.UserAlreadyExistsException;
import com.example.chatConnectSpring.user.application.exceptions.UserNotFoundException;
import com.example.chatConnectSpring.user.domain.model.User;
import com.example.chatConnectSpring.user.domain.port.in.UserUseCase;
import com.example.chatConnectSpring.user.domain.port.out.UserStoragePort;
import com.example.chatConnectSpring.user.domain.port.out.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class UserService implements UserUseCase {

    private final UsuarioRepository usuarioRepository;
    private final UserStoragePort userStoragePort;
    
    @Autowired
    public UserService(
            UsuarioRepository usuarioRepository,
            UserStoragePort userStoragePort
    ) {
        this.usuarioRepository = usuarioRepository;
        this.userStoragePort = userStoragePort;
    }

    public UserService(UsuarioRepository usuarioRepository) {
        this(usuarioRepository, null);
    }
    
    @Override
    public List<User> findUsersByUsersId(List<UUID> usersId) {
        return usuarioRepository.findByIds(usersId);
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
    public User create(User user) {
        return usuarioRepository.create(user);
    }

    @Override
    @Transactional
    public User update(UUID id, String username, String email) {
        User user = usuarioRepository.findById(id);
        if (user == null) {
            throw new UserNotFoundException("User not found with id: " + id);
        }

        String nextUsername = username != null ? username.trim() : null;
        String nextEmail = email != null ? email.trim() : null;

        if (nextUsername != null && nextUsername.isEmpty()) nextUsername = null;
        if (nextEmail != null && nextEmail.isEmpty()) nextEmail = null;

        if (nextUsername != null && (nextUsername.length() < 2 || nextUsername.length() > 30)) {
            throw new IllegalArgumentException("username deve ter entre 2 e 30 caracteres");
        }

        if (nextEmail != null) {
            var existing = usuarioRepository.findByEmail(nextEmail);
            if (existing != null && !Objects.equals(existing.getId(), id)) {
                throw new UserAlreadyExistsException("email já em uso");
            }
        }

        boolean changed = false;
        if (nextUsername != null && !Objects.equals(user.getUsername(), nextUsername)) {
            user.setUsername(nextUsername);
            changed = true;
        }
        if (nextEmail != null && !Objects.equals(user.getEmail(), nextEmail)) {
            user.setEmail(nextEmail);
            changed = true;
        }

        if (changed) {
            return usuarioRepository.save(user);
        }

        return user;
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> search(String q, UUID excludeId, int limit) {
        if (q == null || q.trim().length() < 2) {
            throw new IllegalArgumentException("Parâmetro de busca deve ter ao menos 2 caracteres.");
        }
        if (q.trim().length() > 50) {
            throw new IllegalArgumentException("Parâmetro de busca deve ter no máximo 50 caracteres.");
        }
        return usuarioRepository.searchByUsernameOrEmail(q.trim(), excludeId, limit);
    }

    @Override
    @Transactional(readOnly = true)
    public String generateAvatarUploadUrl(UUID userId, GenerateUserAvatarUploadUrlCommand command) {
        User user = usuarioRepository.findById(userId);
        if (user == null) {
            throw new UserNotFoundException("User not found with id: " + userId);
        }

        String extension = GetExtensionFromContentType.get(command.contentType());
        String objectKey = String.format("uploads/user/avatar/%s%s", userId, extension);

        return userStoragePort.generatePresignedUploadUrl(objectKey, command.contentType(), Duration.ofMinutes(10));
    }
}
