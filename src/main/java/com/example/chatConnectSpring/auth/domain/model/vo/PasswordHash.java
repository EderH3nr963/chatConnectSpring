package com.example.chatConnectSpring.auth.domain.model.vo;

import java.util.Objects;

public final class PasswordHash {

    private final String value;

    private PasswordHash(String value) {
        this.value = value;
    }

    public static PasswordHash of(String hash) {
        if (hash == null || hash.isBlank()) {
            throw new IllegalArgumentException("Password hash must not be blank");
        }
        return new PasswordHash(hash);
    }

    public String value() {
        return value;
    }
    
    public boolean matches(RawPassword raw, PasswordHasher hasher) {
        return hasher.matches(raw, this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PasswordHash other)) return false;
        return value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "******";
    }
}
