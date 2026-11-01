package com.example.chatConnectSpring.user.domain.port.in;

import com.example.chatConnectSpring.user.application.commands.GenerateUserAvatarUploadUrlCommand;
import com.example.chatConnectSpring.user.domain.model.User;

import java.util.List;
import java.util.UUID;

public interface UserUseCase {
    User create(User user);

    User update(UUID id, String username, String email);

    List<User> findUsersByUsersId(List<UUID> usersId);

    User findByEmail(String email);

    User findById(UUID id);

    List<User> findAll();

    void delete(UUID id);

    String generateAvatarUploadUrl(UUID userId, GenerateUserAvatarUploadUrlCommand command);

    List<User> search(String q, UUID excludeId, int limit);
}
