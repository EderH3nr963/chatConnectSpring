package com.example.chatConnectSpring.auth.application.commands;

public record PreRegisterCommand(
        String email,
        String password,
        String username
) {}
