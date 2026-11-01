package com.example.chatConnectSpring.auth.domain.model.vo;

public final class AccessTokenString {

    private final String value;

    private AccessTokenString(String value) {
        this.value = value;
    }

    public static AccessTokenString of(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("Access token must not be blank");
        }
        return new AccessTokenString(raw);
    }

    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return "******";
    }
}
