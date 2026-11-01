package com.example.chatConnectSpring.auth.domain.ports.out;

import java.time.Duration;
import java.util.Optional;

public interface VerificationPort {
    void save(String key, String value, Duration ttl);
    Optional<String> get(String key);
    void delete(String key);
}
