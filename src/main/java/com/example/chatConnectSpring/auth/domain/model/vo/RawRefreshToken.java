package com.example.chatConnectSpring.auth.domain.model.vo;

public final class RawRefreshToken {

    private final String value;

    private RawRefreshToken(String value) {
        this.value = value;
    }

    public static RawRefreshToken of(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("Refresh token must not be blank");
        }
        return new RawRefreshToken(raw);
    }

    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return "******";
    }
}
