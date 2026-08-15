package com.example.chatConnectSpring.usuario.infrastructure.adapters.in;

import com.example.chatConnectSpring.shared.security.UserDetailsImpl;
import com.example.chatConnectSpring.usuario.application.mapper.UsuarioMapper;
import com.example.chatConnectSpring.usuario.application.service.UsuarioService;
import com.example.chatConnectSpring.usuario.domain.model.Usuario;
import com.example.chatConnectSpring.usuario.infrastructure.adapters.in.dto.request.AtualizarSenhaRequestDTO;
import com.example.chatConnectSpring.usuario.infrastructure.adapters.in.dto.request.AtualizarUsuarioRequestDTO;
import com.example.chatConnectSpring.usuario.infrastructure.adapters.in.dto.response.UsuarioResponseDTO;
import jakarta.validation.Valid;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) { this.usuarioService = usuarioService; }

    @GetMapping("/me")
    public UsuarioResponseDTO meuPerfil(@AuthenticationPrincipal UserDetailsImpl principal) {
        return UsuarioMapper.toDTO(usuarioService.findByEmail(principal.getUsername()));
    }

    @PatchMapping("/me")
    public UsuarioResponseDTO atualizar(@AuthenticationPrincipal UserDetailsImpl principal,
                                        @Valid @RequestBody AtualizarUsuarioRequestDTO request) {
        return UsuarioMapper.toDTO(usuarioService.update(principal.getId(), request.username(), request.email()));
    }
    
    @PatchMapping("/me/confirm-change-email/{token}")
    public UsuarioResponseDTO confirmChangeEmail(@AuthenticationPrincipal UserDetailsImpl principal, @Param("token") String token) {
        Usuario usuario = usuarioService.confirmEmailChange(principal.getId(), token);
        
        return UsuarioMapper.toDTO(usuario);
    }

    @PatchMapping("/me/senha")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void atualizarSenha(@AuthenticationPrincipal UserDetailsImpl principal,
                               @Valid @RequestBody AtualizarSenhaRequestDTO request) {
        usuarioService.updatePassword(principal.getId(), request.senhaAtual(), request.novaSenha());
    }

    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@AuthenticationPrincipal UserDetailsImpl principal) {
        usuarioService.delete(usuarioService.findByEmail(principal.getUsername()).getId());
    }
}
