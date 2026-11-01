package com.example.chatConnectSpring.auth.application.events;

import java.util.UUID;

public record UserWebSocketConnectedEvent(UUID userId) {}
