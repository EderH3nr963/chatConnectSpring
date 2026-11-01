package com.example.chatConnectSpring.auth.domain.model.vo;

import java.util.Objects;
import java.util.regex.Pattern;

public final class Email {

    private static final Pattern PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final String value;

    private Email(String value) {
        this.value = value;
    }

    public static Email of(String raw) {
        String normalized = normalize(raw);
        ensureValid(normalized);
        return new Email(normalized);
    }

    public String value() {
        return value;
    }

    private static String normalize(String raw) {
        if (raw == null) {
            throw new IllegalArgumentException("Email must not be null");
        }
        return raw.trim().toLowerCase();
    }

    private static void ensureValid(String candidate) {
        if (candidate.isBlank() || !PATTERN.matcher(candidate).matches()) {
            throw new IllegalArgumentException("Invalid email: " + candidate);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email other)) return false;
        return value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
