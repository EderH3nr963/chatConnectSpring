package com.example.chatConnectSpring.auth.infrastructure.adapters.in.http.dto.response;

import com.example.chatConnectSpring.auth.application.responses.LoginResponse;
import com.example.chatConnectSpring.user.infrastructure.adapters.in.http.dto.response.UserResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;

@Schema(description = "Resposta de autenticação — contém apenas o access token e dados do usuário. O refresh token é entregue via cookie HttpOnly.")
public record LoginResponseDTO(

        @Schema(description = "JWT de acesso (Bearer)", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI...")
        String accessToken,

        @Schema(description = "Expiração do access token (UTC)", example = "2026-09-28T14:30:00Z")
        OffsetDateTime expiresAt,

        @Schema(description = "Dados do usuário autenticado")
        UserResponseDTO user,

        @Schema(description = "true = 2FA ativo, frontend deve pedir código de verificação; false = já logado")
        boolean hasVerifyCode
) {

    public static LoginResponseDTO from(LoginResponse domain) {
        if (domain.hasVerifyCode()) {
            return new LoginResponseDTO(
                    null,
                    null,
                    domain.user(),
                    true
            );
        }
        return new LoginResponseDTO(
                domain.accessToken().value(),
                domain.accessToken().expiresAt(),
                domain.user(),
                false
        );
    }
}
