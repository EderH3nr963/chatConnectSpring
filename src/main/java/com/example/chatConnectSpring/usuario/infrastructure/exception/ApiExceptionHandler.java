package com.example.chatConnectSpring.usuario.infrastructure.exception;

import com.example.chatConnectSpring.usuario.application.service.ConflitoUsuarioException;
import com.example.chatConnectSpring.usuario.application.service.CredenciaisInvalidasException;
import com.example.chatConnectSpring.usuario.application.service.UsuarioNaoEncontradoException;
import com.example.chatConnectSpring.chat.application.exceptions.ChatAccessDeniedException;
import com.example.chatConnectSpring.chat.application.exceptions.ChatNotFoundException;
import com.example.chatConnectSpring.chat.application.exceptions.InvalidChatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(UsuarioNaoEncontradoException.class)
    public ResponseEntity<Map<String, String>> naoEncontrado(UsuarioNaoEncontradoException ex) {
        return response(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(ConflitoUsuarioException.class)
    public ResponseEntity<Map<String, String>> confict(ConflitoUsuarioException ex) {
        return response(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler({CredenciaisInvalidasException.class, BadCredentialsException.class})
    public ResponseEntity<Map<String, String>> naoAutorizado(RuntimeException ex) {
        return response(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(ChatNotFoundException.class)
    public ResponseEntity<Map<String, String>> chatNaoEncontrado(ChatNotFoundException ex) {
        return response(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler({InvalidChatException.class, ChatAccessDeniedException.class, IllegalArgumentException.class})
    public ResponseEntity<Map<String, String>> badRequest(RuntimeException ex) {
        return response(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    private ResponseEntity<Map<String, String>> response(HttpStatus status, String mensagem) {
        return ResponseEntity.status(status).body(Map.of("mensagem", mensagem));
    }
}
