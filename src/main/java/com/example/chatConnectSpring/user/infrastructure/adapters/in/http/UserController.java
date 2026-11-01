package com.example.chatConnectSpring.user.infrastructure.adapters.in.http;

import com.example.chatConnectSpring.auth.infrastructure.adapters.in.http.dto.request.UpdateEmailRequestDTO;
import com.example.chatConnectSpring.auth.infrastructure.adapters.in.http.dto.request.VerifyEmailRequest;
import com.example.chatConnectSpring.auth.infrastructure.security.UserDetailsImpl;
import com.example.chatConnectSpring.chat.infrastructure.adapters.in.http.ChatController;
import com.example.chatConnectSpring.user.application.commands.GenerateUserAvatarUploadUrlCommand;
import com.example.chatConnectSpring.user.domain.port.in.EmailUpdateUseCase;
import com.example.chatConnectSpring.user.domain.port.in.UserUseCase;
import com.example.chatConnectSpring.user.domain.port.out.UserStoragePort;
import com.example.chatConnectSpring.user.infrastructure.adapters.in.http.dto.request.GenerateUserAvatarUploadUrlRequest;
import com.example.chatConnectSpring.user.infrastructure.adapters.in.http.dto.request.UpdateUsernameRequestDTO;
import com.example.chatConnectSpring.user.infrastructure.adapters.in.http.dto.response.PresignedUrlResponse;
import com.example.chatConnectSpring.user.infrastructure.adapters.in.http.dto.response.UserResponseDTO;
import com.example.chatConnectSpring.user.infrastructure.mapper.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.example.chatConnectSpring.user.infrastructure.adapters.in.http.dto.request.UpdateProfileRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Tag(name = "Users", description = "Operações de gerenciamento de usuários e perfil")
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserUseCase userUseCase;
    private final EmailUpdateUseCase emailUpdateUseCase;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserStoragePort userStoragePort;

    public UserController(UserUseCase userUseCase, UserStoragePort userStoragePort, EmailUpdateUseCase emailUpdateUseCase) {
        this.userUseCase = userUseCase;
        this.userStoragePort = userStoragePort;
        this.emailUpdateUseCase = emailUpdateUseCase;
    }

    @GetMapping("/search")
    @Operation(
            summary = "Buscar usuários por nome ou e-mail",
            description = "Busca usuários por username ou e-mail (case-insensitive, contém). Exclui o próprio usuário autenticado. Requer ao menos 2 caracteres."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso (lista pode estar vazia)"),
            @ApiResponse(responseCode = "400", description = "Parâmetro de busca inválido"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<List<UserResponseDTO>> search(
            @Parameter(description = "ID do usuário autenticado", hidden = true)
            @AuthenticationPrincipal UserDetailsImpl usuarioDetails,
            @Parameter(description = "Termo de busca (username ou e-mail)", example = "joao")
            @RequestParam("q") String q,
            @Parameter(description = "Limite de resultados (1-50, padrão 20)")
            @RequestParam(value = "limit", required = false, defaultValue = "20") int limit
    ) {
        List<UserResponseDTO> result = userUseCase.search(q, usuarioDetails.getId(), limit).stream()
                .map(u -> UserMapper.toResponseDTO(u, userStoragePort))
                .toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/me")
    @Operation(summary = "Perfil do usuário autenticado", description = "Retorna o próprio perfil.")
    public ResponseEntity<UserResponseDTO> me(@AuthenticationPrincipal UserDetailsImpl usuarioDetails) {
        log.debug("HTTP GET /api/user/me by userId={}", usuarioDetails.getId());
        var user = userUseCase.findById(usuarioDetails.getId());
        if (user == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(UserMapper.toResponseDTO(user, userStoragePort));
    }

    @Operation(summary = "Atualizar username")
    @PatchMapping("/me/username")
    public ResponseEntity<UserResponseDTO> updateUsername(
            @AuthenticationPrincipal UserDetailsImpl usuarioDetails,
            @Valid @RequestBody UpdateUsernameRequestDTO body
    ) {
        var updated = userUseCase.update(usuarioDetails.getId(), body.username(), null);
        return ResponseEntity.ok(UserMapper.toResponseDTO(updated, userStoragePort));
    }

    @Operation(summary = "Solicitar alteração de e-mail")
    @PostMapping("/me/email-update")
    public ResponseEntity<Void> requestEmailUpdate(
            @AuthenticationPrincipal UserDetailsImpl usuarioDetails,
            @Valid @RequestBody UpdateEmailRequestDTO body
    ) {
        emailUpdateUseCase.requestEmailUpdate(usuarioDetails.getId(), body.newEmail());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Confirmar alteração de e-mail com código")
    @PostMapping("/me/email-update/verify")
    public ResponseEntity<Void> verifyEmailUpdate(
            @AuthenticationPrincipal UserDetailsImpl usuarioDetails,
            @Valid @RequestBody VerifyEmailRequest body
    ) {
        emailUpdateUseCase.verifyEmailUpdate(usuarioDetails.getId(), body.code());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Buscar usuário por ID", description = "Retorna o perfil público do usuário.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<UserResponseDTO> findById(@PathVariable UUID userId) {
        var user = userUseCase.findById(userId);
        if (user == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(UserMapper.toResponseDTO(user, userStoragePort));
    }

    @PostMapping("/avatar/upload-url")
    @Operation(
            summary = "Gerar URL pré-assinada para upload de avatar do usuário",
            description = "Gera uma URL pré-assinada do S3 para permitir o upload direto do avatar pelo usuário autenticado."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "URL pré-assinada gerada com sucesso",
                    content = @Content(schema = @Schema(implementation = PresignedUrlResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos ou tipo de conteúdo não suportado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<PresignedUrlResponse> generateAvatarUploadUrl(
            @Parameter(description = "ID do usuário autenticado no sistema", hidden = true)
            @AuthenticationPrincipal UserDetailsImpl usuarioDetails,
            @Valid @RequestBody GenerateUserAvatarUploadUrlRequest request
    ) {
        GenerateUserAvatarUploadUrlCommand command = new GenerateUserAvatarUploadUrlCommand(request.contentType());
        String uploadUrl = userUseCase.generateAvatarUploadUrl(usuarioDetails.getId(), command);
        return ResponseEntity.ok(new PresignedUrlResponse(uploadUrl));
    }
}
