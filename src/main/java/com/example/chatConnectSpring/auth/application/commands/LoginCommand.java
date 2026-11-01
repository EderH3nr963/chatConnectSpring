package com.example.chatConnectSpring.auth.application.commands;

public record LoginCommand (
        String email,
        String password
) {
}
