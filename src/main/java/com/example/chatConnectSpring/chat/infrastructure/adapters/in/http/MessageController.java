package com.example.chatConnectSpring.chat.infrastructure.adapters.in.http;

import com.example.chatConnectSpring.chat.application.commands.EditMessageCommand;
import com.example.chatConnectSpring.chat.application.commands.SendMessageCommand;
import com.example.chatConnectSpring.chat.domain.model.Message;
import com.example.chatConnectSpring.chat.domain.ports.in.DeleteMessageUseCase;
import com.example.chatConnectSpring.chat.domain.ports.in.EditMessageUseCase;
import com.example.chatConnectSpring.chat.domain.ports.in.FindMessagesByChatIdUseCase;
import com.example.chatConnectSpring.chat.domain.ports.in.SendMessageUseCase;
import com.example.chatConnectSpring.chat.infrastructure.adapters.in.http.dto.request.EditMessageRequestDTO;
import com.example.chatConnectSpring.chat.infrastructure.adapters.in.http.dto.request.SendMessageRequestDTO;
import com.example.chatConnectSpring.chat.infrastructure.adapters.in.http.dto.response.MessageResponseDTO;
import com.example.chatConnectSpring.chat.infrastructure.mapper.MessageMapper;
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

@Tag(name = "Mensagens", description = "Operações de envio, edição, exclusão e consulta de mensagens")
@RestController
@RequestMapping("/api/chat/{chatId}/messages")
@AllArgsConstructor
public class MessageController {

    private final SendMessageUseCase sendMessageUseCase;
    private final EditMessageUseCase editMessageUseCase;
    private final DeleteMessageUseCase deleteMessageUseCase;
    private final FindMessagesByChatIdUseCase findMessagesByChatIdUseCase;

    @PostMapping
    @Operation(summary = "Enviar mensagem para um chat")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Mensagem enviada", content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
            @ApiResponse(responseCode = "403", description = "Sem permissão (não é participante)", content = @Content)
    })
    public ResponseEntity<MessageResponseDTO> send(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID chatId,
            @Valid @RequestBody SendMessageRequestDTO dto
    ) {
        Message message = sendMessageUseCase.send(
                userDetails.getId(),
                new SendMessageCommand(chatId, dto.content())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(MessageMapper.toDTO(message));
    }

    @GetMapping
    @Operation(summary = "Listar histórico de mensagens do chat")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de mensagens", content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Sem permissão", content = @Content)
    })
    public ResponseEntity<List<MessageResponseDTO>> findByChatId(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID chatId
    ) {
        List<MessageResponseDTO> messages = findMessagesByChatIdUseCase.findMessagesByChatId(userDetails.getId(), chatId)
                .stream()
                .map(MessageMapper::toDTO)
                .toList();
        return ResponseEntity.ok(messages);
    }

    @PatchMapping("/{messageId}")
    @Operation(summary = "Editar mensagem")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Mensagem editada", content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
            @ApiResponse(responseCode = "403", description = "Sem permissão (apenas autor)", content = @Content),
            @ApiResponse(responseCode = "404", description = "Mensagem não encontrada", content = @Content)
    })
    public ResponseEntity<MessageResponseDTO> edit(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID chatId,
            @PathVariable UUID messageId,
            @Valid @RequestBody EditMessageRequestDTO dto
    ) {
        Message message = editMessageUseCase.edit(
                userDetails.getId(),
                new EditMessageCommand(messageId, dto.content())
        );
        return ResponseEntity.ok(MessageMapper.toDTO(message));
    }

    @DeleteMapping("/{messageId}")
    @Operation(summary = "Excluir mensagem")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Mensagem excluída"),
            @ApiResponse(responseCode = "403", description = "Sem permissão (apenas autor ou admin)", content = @Content),
            @ApiResponse(responseCode = "404", description = "Mensagem não encontrada", content = @Content)
    })
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID chatId,
            @PathVariable UUID messageId
    ) {
        deleteMessageUseCase.delete(userDetails.getId(), messageId);
        return ResponseEntity.noContent().build();
    }
}
