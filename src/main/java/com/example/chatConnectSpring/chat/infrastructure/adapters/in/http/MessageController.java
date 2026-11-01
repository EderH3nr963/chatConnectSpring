package com.example.chatConnectSpring.chat.infrastructure.adapters.in.http;

import com.example.chatConnectSpring.chat.domain.model.message.Message;
import com.example.chatConnectSpring.chat.domain.ports.in.MessageUseCase;
import com.example.chatConnectSpring.chat.domain.ports.out.StoragePort;
import com.example.chatConnectSpring.chat.infrastructure.adapters.in.http.dto.response.MessageResponseDTO;
import com.example.chatConnectSpring.chat.infrastructure.mapper.MessageMapper;
import com.example.chatConnectSpring.auth.infrastructure.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat/{chatId}/messages")
@Tag(name = "Messages", description = "Operacoes de consulta de mensagens")
public class MessageController {
    private final MessageUseCase messageUseCase;
    private final StoragePort storagePort;

    public MessageController(MessageUseCase messageUseCase, StoragePort storagePort) {
        this.messageUseCase = messageUseCase;
        this.storagePort = storagePort;
    }

    @GetMapping
    @Operation(summary = "Listar mensagens de um chat com paginacao")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Mensagens do chat paginadas", content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Usuario nao participa deste chat", content = @Content),
            @ApiResponse(responseCode = "404", description = "Chat nao encontrado", content = @Content)
    })
    public ResponseEntity<Slice<MessageResponseDTO>> findMessages(
            @AuthenticationPrincipal UserDetailsImpl usuarioDetails,
            @PathVariable UUID chatId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction
    ) {
        Sort.Direction dir = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, sortBy));
        Slice<Message> messages = messageUseCase.findMessagesByChatId(usuarioDetails.getId(), chatId, pageable);
        Slice<MessageResponseDTO> response = messages.map(m -> MessageMapper.toDTO(m, storagePort));
        return ResponseEntity.ok(response);
    }
}
