package com.example.chatConnectSpring.chat.infrastructure.adapters.in.http;

import com.example.chatConnectSpring.chat.application.commands.AddParticipantsCommand;
import com.example.chatConnectSpring.chat.application.commands.CreateGroupChatCommand;
import com.example.chatConnectSpring.chat.application.commands.CreatePrivateChatCommand;
import com.example.chatConnectSpring.chat.application.commands.UpdateChatCommand;
import com.example.chatConnectSpring.chat.domain.model.chat.Chat;
import com.example.chatConnectSpring.chat.domain.ports.in.AddParticipantsUseCase;
import com.example.chatConnectSpring.chat.domain.ports.in.CreateGroupChatUseCase;
import com.example.chatConnectSpring.chat.domain.ports.in.CreatePrivateChatUseCase;
import com.example.chatConnectSpring.chat.domain.ports.in.DeleteChatUseCase;
import com.example.chatConnectSpring.chat.domain.ports.in.FindChatByIdUseCase;
import com.example.chatConnectSpring.chat.domain.ports.in.FindChatsByUserIdUseCase;
import com.example.chatConnectSpring.chat.domain.ports.in.RemoveParticipantUseCase;
import com.example.chatConnectSpring.chat.domain.ports.in.UpdateChatUseCase;
import com.example.chatConnectSpring.chat.infrastructure.adapters.in.http.dto.request.AddParticipantsRequestDTO;
import com.example.chatConnectSpring.chat.infrastructure.adapters.in.http.dto.request.CreateGroupChatRequestDTO;
import com.example.chatConnectSpring.chat.infrastructure.adapters.in.http.dto.request.CreatePrivateChatRequestDTO;
import com.example.chatConnectSpring.chat.infrastructure.adapters.in.http.dto.request.UpdateChatRequestDTO;
import com.example.chatConnectSpring.chat.infrastructure.adapters.in.http.dto.response.ChatResponseDTO;
import com.example.chatConnectSpring.chat.infrastructure.mapper.ChatMapper;
import com.example.chatConnectSpring.shared.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Chats", description = "Operações de criação, consulta e manutenção de chats")
@RestController
@RequestMapping("/api/chat")
@AllArgsConstructor
public class ChatController {
    private final CreateGroupChatUseCase createGroupChatUseCase;
    private final CreatePrivateChatUseCase createPrivateChatUseCase;
    private final FindChatByIdUseCase findChatByIdUseCase;
    private final FindChatsByUserIdUseCase findChatsByUserIdUseCase;
    private final UpdateChatUseCase updateChatUseCase;
    private final DeleteChatUseCase deleteChatUseCase;
    private final AddParticipantsUseCase addParticipantsUseCase;
    private final RemoveParticipantUseCase removeParticipantUseCase;

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
        Chat chat = createGroupChatUseCase.createGroupChat(
                usuarioDetails.getId(),
                new CreateGroupChatCommand(dto.title(), dto.description(), dto.participants())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(ChatMapper.toDTO(chat));
    }

    @PostMapping("/private")
    @Operation(summary = "Criar chat privado")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Chat criado", content = @Content(schema = @Schema(implementation = ChatResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    })
    public ResponseEntity<ChatResponseDTO> createPrivate(
            @AuthenticationPrincipal UserDetailsImpl usuarioDetails,
            @Valid @RequestBody CreatePrivateChatRequestDTO dto
    ) {
        Chat chat = createPrivateChatUseCase.createPrivateChat(
                usuarioDetails.getId(),
                new CreatePrivateChatCommand(new AddParticipantsCommand(dto.users(), null))
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(ChatMapper.toDTO(chat));
    }

    @GetMapping
    @Operation(summary = "Listar chats do usuário autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de chats", content = @Content(schema = @Schema(implementation = ChatResponseDTO.class)))
    })
    public ResponseEntity<List<ChatResponseDTO>> findAllByUser(@AuthenticationPrincipal UserDetailsImpl usuarioDetails) {
        List<ChatResponseDTO> chats = findChatsByUserIdUseCase.findChatsByUSerId(usuarioDetails.getId())
                .stream()
                .map(ChatMapper::toDTO)
                .toList();
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
        Chat chat = findChatByIdUseCase.findChatById(usuarioDetails.getId(), chatId);
        return ResponseEntity.ok(ChatMapper.toDTO(chat));
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
        Chat chat = updateChatUseCase.update(
                usuarioDetails.getId(),
                new UpdateChatCommand(dto.title(), dto.description(), chatId)
        );
        return ResponseEntity.ok(ChatMapper.toDTO(chat));
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
        deleteChatUseCase.delete(usuarioDetails.getId(), chatId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{chatId}/participants")
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
        addParticipantsUseCase.add(usuarioDetails.getId(), new AddParticipantsCommand(dto.users(), chatId));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{chatId}/participants/{participantId}")
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
        removeParticipantUseCase.remove(usuarioDetails.getId(), participantId, chatId);
        return ResponseEntity.noContent().build();
    }
}
