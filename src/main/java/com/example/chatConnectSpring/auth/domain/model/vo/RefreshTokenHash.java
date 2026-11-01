package com.example.chatConnectSpring.auth.domain.model.vo;

import java.util.Objects;

public final class RefreshTokenHash {

    private final String value;

    private RefreshTokenHash(String value) {
        this.value = value;
    }

    public static RefreshTokenHash of(String hash) {
        if (hash == null || hash.isBlank()) {
            throw new IllegalArgumentException("Refresh token hash must not be blank");
        }
        return new RefreshTokenHash(hash);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RefreshTokenHash other)) return false;
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
