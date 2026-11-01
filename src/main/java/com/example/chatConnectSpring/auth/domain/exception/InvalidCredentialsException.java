package com.example.chatConnectSpring.auth.domain.exception;

import org.springframework.security.authentication.BadCredentialsException;

/**
 * Exceção de domínio semântica para credenciais inválidas.
 */
public class InvalidCredentialsException extends BadCredentialsException {

    public InvalidCredentialsException() {
        super("Invalid email or password");
    }
}
