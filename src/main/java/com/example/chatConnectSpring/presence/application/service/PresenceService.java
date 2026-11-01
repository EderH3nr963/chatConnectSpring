package com.example.chatConnectSpring.presence.application.service;

import com.example.chatConnectSpring.presence.domain.model.Presence;
import com.example.chatConnectSpring.presence.domain.ports.in.PresenceUseCase;
import com.example.chatConnectSpring.presence.domain.ports.out.PresenceNotificationPort;
import com.example.chatConnectSpring.presence.domain.ports.out.PresenceRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PresenceService implements PresenceUseCase {

    private final PresenceRepository presenceRepository;
    private final PresenceNotificationPort notificationPort;

    public PresenceService(PresenceRepository presenceRepository, PresenceNotificationPort notificationPort) {
        this.presenceRepository = presenceRepository;
        this.notificationPort = notificationPort;
    }

    @Override
    public void markOnline(UUID userId) {
        Presence presence = Presence.online(userId);
        presenceRepository.save(presence);
        notificationPort.notifyFriends(userId, presence);
    }

    @Override
    public void markOffline(UUID userId) {
        Presence presence = Presence.offline(userId);
        presenceRepository.save(presence);
        notificationPort.notifyFriends(userId, presence);
    }

    @Override
    public void heartbeat(UUID userId) {
        presenceRepository.findByUserId(userId).ifPresentOrElse(
                existing -> presenceRepository.save(existing.heartbeat()),
                () -> markOnline(userId)
        );
    }

    @Override
    public Presence get(UUID userId) {
        return presenceRepository.findByUserId(userId).orElse(Presence.offline(userId));
    }

    @Override
    public boolean isOnline(UUID userId) {
        return presenceRepository.isOnline(userId);
    }
}
