package com.example.chatConnectSpring.user.infrastructure.adapters.in.http;

import com.example.chatConnectSpring.user.domain.model.User;
import com.example.chatConnectSpring.user.domain.port.in.DeleteUsuarioUseCase;
import com.example.chatConnectSpring.user.domain.port.in.FindByClerkUserIdUseCase;
import com.example.chatConnectSpring.user.domain.port.in.SyncClerkUserUseCase;
import com.example.chatConnectSpring.user.infrastructure.adapters.in.http.dto.clerk.ClerkUserDataDTO;
import com.example.chatConnectSpring.user.infrastructure.adapters.in.http.dto.clerk.ClerkUserCreatedWebhookDTO;
import com.example.chatConnectSpring.user.infrastructure.adapters.in.http.dto.clerk.ClerkUserDeletedDataDTO;
import com.example.chatConnectSpring.user.infrastructure.adapters.in.http.dto.clerk.ClerkUserDeletedWebhookDTO;
import com.example.chatConnectSpring.user.infrastructure.adapters.in.http.dto.clerk.ClerkUserUpdatedWebhookDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "Clerk Webhooks", description = "Endpoints para recepção e sincronização de eventos do webhook do Clerk")
@RestController
@RequestMapping("/api/webhooks/clerk")
@RequiredArgsConstructor
public class ClerkWebhookController {

    private final SyncClerkUserUseCase syncClerkUserUseCase;
    private final FindByClerkUserIdUseCase findByClerkUserIdUseCase;
    private final DeleteUsuarioUseCase deleteUsuarioUseCase;
    private final ObjectMapper objectMapper = new ObjectMapper();

    
    @PostMapping("/user-created")
    @Operation(summary = "Processar evento específico de criação de usuário do Clerk (user.created)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário criado/sincronizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Payload inválido")
    })
    public ResponseEntity<Void> handleUserCreated(@RequestBody ClerkUserCreatedWebhookDTO payload) {
        ClerkUserDataDTO data = payload.data();
        processUserUpsert(data);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/user-updated")
    @Operation(summary = "Processar evento específico de atualização de usuário do Clerk (user.updated)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Payload inválido")
    })
    public ResponseEntity<Void> handleUserUpdated(@RequestBody ClerkUserUpdatedWebhookDTO payload) {
        ClerkUserDataDTO data = payload.data();
        processUserUpsert(data);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/user-deleted")
    @Operation(summary = "Processar evento específico de exclusão de usuário do Clerk (user.deleted)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário excluído com sucesso"),
            @ApiResponse(responseCode = "400", description = "Payload inválido")
    })
    public ResponseEntity<Void> handleUserDeleted(@RequestBody ClerkUserDeletedWebhookDTO payload) {
        ClerkUserDeletedDataDTO data = payload.data();
        processUserDeleted(data);
        return ResponseEntity.ok().build();
    }

    private void processUserUpsert(ClerkUserDataDTO data) {
        if (data == null) {
            return;
        }
        String clerkUserId = data.id();
        String email = data.getPrimaryEmail();
        String username = data.resolveUsername();

        syncClerkUserUseCase.syncFromClerk(clerkUserId, email, username);
    }

    private void processUserDeleted(ClerkUserDeletedDataDTO data) {
        if (data == null || data.id() == null) {
            return;
        }
        String clerkUserId = data.id();
        User user = findByClerkUserIdUseCase.findByClerkUserId(clerkUserId);
        if (user != null && user.getId() != null) {
            deleteUsuarioUseCase.delete(user.getId());
        } else {
            deleteUsuarioUseCase.deleteByClerkUserId(clerkUserId);
        }
    }
}
