package com.example.chatConnectSpring.chat.infrastructure.adapters.in.http;

import com.example.chatConnectSpring.chat.application.commands.AddParticipantsCommand;
import com.example.chatConnectSpring.chat.application.commands.CreateGroupChatCommand;
import com.example.chatConnectSpring.chat.application.commands.GenerateGroupImageUploadUrlCommand;
import com.example.chatConnectSpring.chat.application.commands.UpdateChatCommand;
import com.example.chatConnectSpring.chat.domain.model.chat.Chat;
import com.example.chatConnectSpring.chat.domain.model.chatParticipant.ChatParticipant;
import com.example.chatConnectSpring.chat.domain.ports.in.*;
import com.example.chatConnectSpring.chat.domain.ports.out.StoragePort;
import com.example.chatConnectSpring.chat.infrastructure.adapters.in.http.dto.request.*;
import com.example.chatConnectSpring.chat.infrastructure.adapters.in.http.dto.response.ChatParticipantResponseDTO;
import com.example.chatConnectSpring.chat.infrastructure.adapters.in.http.dto.response.ChatResponseDTO;
import com.example.chatConnectSpring.chat.domain.ports.out.projections.ChatSummaryDto;
import com.example.chatConnectSpring.chat.infrastructure.adapters.in.http.dto.response.PresignedUrlResponse;
import com.example.chatConnectSpring.chat.infrastructure.mapper.ChatMapper;
import com.example.chatConnectSpring.auth.infrastructure.security.UserDetailsImpl;
import com.example.chatConnectSpring.chat.infrastructure.mapper.ChatParticipantMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Chats", description = "Operações de criação, consulta e manutenção de chats")
@RestController
@RequestMapping("/api/chat")
public class ChatController {
    private static final Logger log = LoggerFactory.getLogger(ChatController.class);
    private final ChatUseCase chatUseCase;
    private final ChatParticipantUseCase chatParticipantUseCase;
    private final StoragePort storagePort;

    public ChatController(ChatUseCase chatUseCase, ChatParticipantUseCase chatParticipantUseCase, StoragePort storagePort) {
        this.chatUseCase = chatUseCase;
        this.chatParticipantUseCase = chatParticipantUseCase;
        this.storagePort = storagePort;
    }

    @PostMapping("/group")
    @Operation(summary = "Criar chat em grupo")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Chat criado", content = @Content(schema = @Schema(implementation = ChatResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    })
    public ResponseEntity<ChatResponseDTO> createGroup(
            @AuthenticationPrincipal UserDetailsImpl usuarioDetails,
            @Valid @RequestBody CreateGroupChatRequestDTO dto
    ) {
        log.info("HTTP POST /api/chat/group by userId={}", usuarioDetails.getId());
        Chat chat = chatUseCase.createGroupChat(
                usuarioDetails.getId(),
                new CreateGroupChatCommand(dto.name(), dto.description(), dto.memberIds())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(ChatMapper.toDTO(chat, storagePort));
    }

    @GetMapping
    @Operation(summary = "Listar chats do usuário autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de chats", content = @Content(schema = @Schema(implementation = ChatSummaryDto.class)))
    })
    public ResponseEntity<List<ChatSummaryDto>> findAllByUser(@AuthenticationPrincipal UserDetailsImpl usuarioDetails) {
        log.debug("HTTP GET /api/chat by userId={}", usuarioDetails.getId());
        List<ChatSummaryDto> chats = chatUseCase.findChatsSummaryByUserId(usuarioDetails.getId());
        return ResponseEntity.ok(chats);
    }

    @GetMapping("/{chatId}")
    @Operation(summary = "Buscar chat por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Chat encontrado", content = @Content(schema = @Schema(implementation = ChatResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Chat não encontrado", content = @Content)
    })
    public ResponseEntity<ChatResponseDTO> findById(
            @AuthenticationPrincipal UserDetailsImpl usuarioDetails,
            @PathVariable UUID chatId
    ) {
        log.debug("HTTP GET /api/chat/{} by userId={}", chatId, usuarioDetails.getId());
        Chat chat = chatUseCase.findChatById(usuarioDetails.getId(), chatId);
        return ResponseEntity.ok(ChatMapper.toDTO(chat, storagePort));
    }

    @PatchMapping("/{chatId}")
    @Operation(summary = "Atualizar chat")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Chat atualizado", content = @Content(schema = @Schema(implementation = ChatResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
            @ApiResponse(responseCode = "403", description = "Sem permissão", content = @Content)
    })
    public ResponseEntity<ChatResponseDTO> update(
            @AuthenticationPrincipal UserDetailsImpl usuarioDetails,
            @PathVariable UUID chatId,
            @Valid @RequestBody UpdateChatRequestDTO dto
    ) {
        log.info("HTTP PATCH /api/chat/{} by userId={}", chatId, usuarioDetails.getId());
        Chat chat = chatUseCase.update(
                usuarioDetails.getId(),
                new UpdateChatCommand(dto.title(), dto.description(), chatId)
        );
        return ResponseEntity.ok(ChatMapper.toDTO(chat, storagePort));
    }

    @DeleteMapping("/{chatId}")
    @Operation(summary = "Excluir chat")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Chat excluído"),
            @ApiResponse(responseCode = "403", description = "Sem permissão", content = @Content),
            @ApiResponse(responseCode = "404", description = "Chat não encontrado", content = @Content)
    })
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal UserDetailsImpl usuarioDetails,
            @PathVariable UUID chatId
    ) {
        log.info("HTTP DELETE /api/chat/{} by userId={}", chatId, usuarioDetails.getId());
        chatUseCase.delete(usuarioDetails.getId(), chatId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{chatId}/memberIds")
    @Operation(summary = "Adicionar participantes ao chat")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Participantes adicionados"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
            @ApiResponse(responseCode = "403", description = "Sem permissão", content = @Content)
    })
    public ResponseEntity<Void> addParticipants(
            @AuthenticationPrincipal UserDetailsImpl usuarioDetails,
            @PathVariable UUID chatId,
            @Valid @RequestBody AddParticipantsRequestDTO dto
    ) {
        log.info("HTTP POST /api/chat/{}/memberIds by userId={}", chatId, usuarioDetails.getId());
        chatParticipantUseCase.add(usuarioDetails.getId(), new AddParticipantsCommand(dto.users(), chatId));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{chatId}/memberIds/{participantId}")
    @Operation(summary = "Remover participante do chat")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Participante removido"),
            @ApiResponse(responseCode = "403", description = "Sem permissão", content = @Content),
            @ApiResponse(responseCode = "404", description = "Participante não encontrado", content = @Content)
    })
    public ResponseEntity<Void> removeParticipant(
            @AuthenticationPrincipal UserDetailsImpl usuarioDetails,
            @PathVariable UUID chatId,
            @PathVariable UUID participantId
    ) {
        chatParticipantUseCase.remove(usuarioDetails.getId(), participantId, chatId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{chatId}/participants")
    @Operation(summary = "Busca todos os usuários do chat especifícado")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = ChatParticipantResponseDTO.class))
            ),
    })
    public ResponseEntity<List<ChatParticipantResponseDTO>> getParticipantsOfChat(
            @Parameter(description = "ID do usuário autenticado no sistema", hidden = true)
            @AuthenticationPrincipal UserDetailsImpl usuarioDetails,
            @Parameter(description = "ID do chat/grupo", required = true)
            @PathVariable UUID chatId
    ) {
        List<ChatParticipant> participants = chatParticipantUseCase.getParticipants(usuarioDetails.getId(), chatId);
    
        return ResponseEntity.ok(participants.stream().map(c -> ChatParticipantMapper.toDTO(c, storagePort)).toList());
    }
    
    @PostMapping("/{chatId}/group-image/upload-url")
    @Operation(summary = "Gera URL pré-assinada para upload da imagem do grupo")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "URL pré-assinada gerada com sucesso",
                    content = @Content(schema = @Schema(implementation = PresignedUrlResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Parâmetros de requisição inválidos ou tipo de conteúdo não suportado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado: o usuário não é administrador do grupo"),
            @ApiResponse(responseCode = "404", description = "Chat não encontrado")
    })
    public ResponseEntity<PresignedUrlResponse> generateGroupImageUploadUrl(
            @Parameter(description = "ID do usuário autenticado no sistema", hidden = true)
            @AuthenticationPrincipal UserDetailsImpl usuarioDetails,
            @Parameter(description = "ID do chat/grupo", required = true)
            @PathVariable UUID chatId,
            @Valid @RequestBody GenerateGroupImageUploadUrlRequest request
    ) {
        GenerateGroupImageUploadUrlCommand command = new GenerateGroupImageUploadUrlCommand(request.contentType());
        String uploadUrl = chatUseCase.generateGroupImageUploadUrl(usuarioDetails.getId(), chatId, command);
        return ResponseEntity.ok(new PresignedUrlResponse(uploadUrl));
    }

    @PatchMapping("/{chatId}/mute")
    @Operation(summary = "Alternar silenciamento de notificações do chat")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Silenciamento alterado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Participante não encontrado")
    })
    public ResponseEntity<Void> toggleMute(
            @AuthenticationPrincipal UserDetailsImpl usuarioDetails,
            @PathVariable UUID chatId
    ) {
        log.info("HTTP PATCH /api/chat/{}/mute by userId={}", chatId, usuarioDetails.getId());
        chatParticipantUseCase.toggleMute(usuarioDetails.getId(), chatId);
        return ResponseEntity.noContent().build();
    }
}
