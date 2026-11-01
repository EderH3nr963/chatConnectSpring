package com.example.chatConnectSpring.auth.domain.model;

import java.util.Collections;
import java.util.List;

public final class RefreshTokenCollection {

    private final List<RefreshToken> tokens;

    public RefreshTokenCollection(List<RefreshToken> tokens) {
        this.tokens = List.copyOf(tokens);
    }

    public List<RefreshToken> all() {
        return Collections.unmodifiableList(tokens);
    }

    public boolean isEmpty() {
        return tokens.isEmpty();
    }

    public int size() {
        return tokens.size();
    }
}
