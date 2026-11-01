package com.example.chatConnectSpring.presence.infrastructure.adapters.in.http;

import com.example.chatConnectSpring.auth.infrastructure.security.UserDetailsImpl;
import com.example.chatConnectSpring.presence.domain.model.Presence;
import com.example.chatConnectSpring.presence.domain.ports.in.PresenceUseCase;
import com.example.chatConnectSpring.presence.infrastructure.adapters.in.http.dto.PresenceResponseDTO;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/presence")
public class PresenceController {

    private final PresenceUseCase presenceUseCase;

    public PresenceController(PresenceUseCase presenceUseCase) {
        this.presenceUseCase = presenceUseCase;
    }

    @GetMapping("/{userId}")
    public PresenceResponseDTO get(@PathVariable UUID userId) {
        Presence presence = presenceUseCase.get(userId);
        return PresenceResponseDTO.from(presence);
    }

    @PostMapping("/heartbeat")
    public void heartbeat(@AuthenticationPrincipal UserDetailsImpl usuarioDetails) {
        presenceUseCase.heartbeat(usuarioDetails.getId());
    }
}
