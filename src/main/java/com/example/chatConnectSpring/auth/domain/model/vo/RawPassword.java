package com.example.chatConnectSpring.auth.domain.model.vo;

import java.util.regex.Pattern;

public final class RawPassword {
    
    private static final Pattern STRONG_PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,30}$"
    );
    
    private final String value;
    
    private RawPassword(String value) {
        this.value = value;
    }
    
    public static RawPassword of(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("Password must not be blank");
        }
        if (!STRONG_PASSWORD_PATTERN.matcher(raw).matches()) {
            throw new IllegalArgumentException(
                    "Password must be 8-30 characters long and include uppercase, lowercase, numbers, and special characters"
            );
        }
        return new RawPassword(raw);
    }
    
    public String value() {
        return value;
    }
    
    @Override
    public String toString() {
        return "******";
    }
}