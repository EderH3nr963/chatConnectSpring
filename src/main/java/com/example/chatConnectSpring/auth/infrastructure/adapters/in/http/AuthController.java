package com.example.chatConnectSpring.auth.infrastructure.adapters.in.http;

import com.example.chatConnectSpring.auth.application.commands.LoginCommand;
import com.example.chatConnectSpring.auth.application.commands.PreRegisterCommand;
import com.example.chatConnectSpring.auth.application.responses.LoginResponse;
import com.example.chatConnectSpring.auth.domain.model.vo.RawRefreshToken;
import com.example.chatConnectSpring.auth.domain.ports.in.*;
import com.example.chatConnectSpring.auth.application.ports.in.Resend2FACodeUseCase;
import com.example.chatConnectSpring.auth.application.ports.in.Toggle2FAUseCase;
import com.example.chatConnectSpring.auth.application.ports.in.UpdatePasswordUseCase;
import com.example.chatConnectSpring.auth.infrastructure.adapters.in.http.dto.request.*;
import com.example.chatConnectSpring.auth.infrastructure.adapters.in.http.dto.response.LoginResponseDTO;
import com.example.chatConnectSpring.auth.infrastructure.security.RefreshTokenCookieService;
import com.example.chatConnectSpring.auth.infrastructure.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "Autenticação, registro, renovação de sessão e logout")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticateUseCase authenticateUseCase;
    private final RegisterUseCase registerUseCase;
    private final RefreshSessionUseCase refreshSessionUseCase;
    private final LogoutUseCase logoutUseCase;
    private final RequestPasswordResetUseCase requestPasswordResetUseCase;
    private final ResetPasswordUseCase resetPasswordUseCase;
    private final UpdatePasswordUseCase updatePasswordUseCase;
    private final Toggle2FAUseCase toggle2FAUseCase;
    private final TwoFactorVerificationUseCase twoFactorVerificationUseCase;
    private final Resend2FACodeUseCase resend2FACodeUseCase;
    private final RefreshTokenCookieService cookieService;

    public AuthController(AuthenticateUseCase authenticateUseCase,
                          RegisterUseCase registerUseCase,
                          RefreshSessionUseCase refreshSessionUseCase,
                          LogoutUseCase logoutUseCase,
                          RequestPasswordResetUseCase requestPasswordResetUseCase,
                          ResetPasswordUseCase resetPasswordUseCase,
                          UpdatePasswordUseCase updatePasswordUseCase,
                          Toggle2FAUseCase toggle2FAUseCase,
                          TwoFactorVerificationUseCase twoFactorVerificationUseCase,
                          Resend2FACodeUseCase resend2FACodeUseCase,
                          RefreshTokenCookieService cookieService) {
        this.authenticateUseCase = authenticateUseCase;
        this.registerUseCase = registerUseCase;
        this.refreshSessionUseCase = refreshSessionUseCase;
        this.logoutUseCase = logoutUseCase;
        this.requestPasswordResetUseCase = requestPasswordResetUseCase;
        this.resetPasswordUseCase = resetPasswordUseCase;
        this.updatePasswordUseCase = updatePasswordUseCase;
        this.toggle2FAUseCase = toggle2FAUseCase;
        this.twoFactorVerificationUseCase = twoFactorVerificationUseCase;
        this.resend2FACodeUseCase = resend2FACodeUseCase;
        this.cookieService = cookieService;
    }

    @Operation(summary = "Ativar ou desativar 2FA")
    @PostMapping("/2fa")
    public ResponseEntity<Void> toggle2FA(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody Toggle2FARequestDTO request
    ) {
        toggle2FAUseCase.toggle(userDetails.getId(), request.active());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Verificar código 2FA e concluir login", description = "Valida e-mail + código 2FA. Em caso de sucesso retorna access token e seta cookies HttpOnly.")
    @PostMapping("/2fa/verify")
    public ResponseEntity<LoginResponseDTO> verify2FA(
            @Valid @RequestBody Verify2FARequestDTO request,
            HttpServletResponse response
    ) {
        LoginResponse result = twoFactorVerificationUseCase.verify(request.email(), request.code());
        cookieService.addRefreshCookies(response, result.refreshToken());
        return ResponseEntity.ok(LoginResponseDTO.from(result));
    }

    @Operation(summary = "Reenviar código 2FA", description = "Gera um novo código de 6 dígitos e envia por e-mail se o usuário tiver 2FA ativo.")
    @PostMapping("/2fa/resend")
    public ResponseEntity<Void> resend2FA(@Valid @RequestBody Resend2FARequestDTO request) {
        resend2FACodeUseCase.resend(request.email());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Solicitar verificação de registro")
    @PostMapping("/pre-register")
    public ResponseEntity<Void> preRegister(@Valid @RequestBody PreRegisterRequestDTO request) {
        registerUseCase.preRegister(new PreRegisterCommand(request.email(), request.password(), request.username()));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Confirmar registro")
    @PostMapping("/verify-registration")
    public ResponseEntity<LoginResponseDTO> verifyRegistration(@Valid @RequestBody VerifyEmailRequest request, HttpServletResponse response) {
        LoginResponse result = registerUseCase.verifyRegistration(request.email(), request.code());
        return ResponseEntity.ok(LoginResponseDTO.from(result));
    }

    @Operation(summary = "Autenticar usuário", description = "Valida e-mail/senha, retorna access token no body e refresh token em cookies HttpOnly.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Autenticado com sucesso", content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request, HttpServletResponse response) {
        LoginCommand command = new LoginCommand(request.email(), request.password());
        LoginResponse result = authenticateUseCase.login(command);
        if (!result.hasVerifyCode()) {
            cookieService.addRefreshCookies(response, result.refreshToken());
        }
        return ResponseEntity.ok(LoginResponseDTO.from(result));
    }

    @Operation(summary = "Renovar sessão", description = "Lê o refresh token do cookie HttpOnly, rotaciona o par de tokens e reenvia novos cookies.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sessão renovada", content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Refresh token inválido/expirado/revogado")
    })
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDTO> refresh(@CookieValue(name = "refreshToken", required = false) String refreshToken,
                                                    HttpServletResponse response) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(401).build();
        }
        LoginResponse result = refreshSessionUseCase.refresh(RawRefreshToken.of(refreshToken));
        cookieService.addRefreshCookies(response, result.refreshToken());
        return ResponseEntity.ok(LoginResponseDTO.from(result));
    }

    @Operation(summary = "Logout", description = "Revoga o refresh token do cookie e limpa os cookies de sessão.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Logout realizado"),
            @ApiResponse(responseCode = "401", description = "Refresh token ausente")
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue(name = "refreshToken", required = false) String refreshToken,
                                       HttpServletResponse response) {
        if (refreshToken != null && !refreshToken.isBlank()) {
            logoutUseCase.logout(RawRefreshToken.of(refreshToken));
        }
        cookieService.clearCookies(response);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Solicitar código de recuperação", description = "Envia um código de 6 dígitos por e-mail. Sempre retorna 204 para não expor se o e-mail existe.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Solicitação processada"),
            @ApiResponse(responseCode = "400", description = "E-mail inválido")
    })
    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO request) {
        requestPasswordResetUseCase.request(request.email());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Redefinir senha", description = "Valida e-mail + código de 6 dígitos e atualiza a senha. Invalida todos os refresh tokens do usuário.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Senha redefinida"),
            @ApiResponse(responseCode = "400", description = "Código inválido/expirado ou senha fraca")
    })
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequestDTO request) {
        resetPasswordUseCase.reset(request.email(), request.code(), request.newPassword());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Atualizar senha do usuário", description = "Valida senha atual e atualiza para a nova senha.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Senha atualizada"),
            @ApiResponse(responseCode = "400", description = "Senha atual incorreta ou nova senha inválida")
    })
    @PutMapping("/password")
    public ResponseEntity<Void> updatePassword(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody UpdatePasswordRequestDTO request
    ) {
        updatePasswordUseCase.updatePassword(userDetails.getId(), request.currentPassword(), request.newPassword());
        return ResponseEntity.noContent().build();
    }
}
