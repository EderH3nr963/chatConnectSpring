package com.example.chatConnectSpring.user.domain.port.in;

import com.example.chatConnectSpring.user.domain.model.User;

import java.util.List;

public interface FindAllUsersUseCase {
    List<User> findAll();
}
