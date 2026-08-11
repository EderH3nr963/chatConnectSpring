package com.example.chatConnectSpring.usuario.application.service;

public class UsuarioNaoEncontradoException extends RuntimeException {
    public UsuarioNaoEncontradoException() { super("Usuario nao encontrado"); }
}
