package com.example.chatConnectSpring.chat.infrastructure.adapters.in.web;

import com.example.chatConnectSpring.chat.application.command.AddParticipantCommand;
import com.example.chatConnectSpring.chat.application.command.CreateChatCommand;
import com.example.chatConnectSpring.chat.application.command.RemoveParticipantCommand;
import com.example.chatConnectSpring.chat.application.command.UpdateTitleChatCommand;
import com.example.chatConnectSpring.chat.domain.port.in.chat.AddParticipantUseCase;
import com.example.chatConnectSpring.chat.domain.port.in.chat.CreateChatUseCase;
import com.example.chatConnectSpring.chat.domain.port.in.chat.FindChatByIdUseCase;
import com.example.chatConnectSpring.chat.domain.port.in.chat.FindChatsByUserIdUseCase;
import com.example.chatConnectSpring.chat.domain.port.in.chat.RemoveParticipantUseCase;
import com.example.chatConnectSpring.chat.domain.port.in.chat.UpdateTitleChatUseCase;
import com.example.chatConnectSpring.chat.infrastructure.adapters.in.web.dto.request.AddParticipantRequestDTO;
import com.example.chatConnectSpring.chat.infrastructure.adapters.in.web.dto.request.CreateChatRequestDTO;
import com.example.chatConnectSpring.chat.infrastructure.adapters.in.web.dto.request.UpdateTitleChatRequestDTO;
import com.example.chatConnectSpring.chat.infrastructure.adapters.in.web.dto.response.ChatParticipantResponseDTO;
import com.example.chatConnectSpring.chat.infrastructure.adapters.in.web.dto.response.ChatResponseDTO;
import com.example.chatConnectSpring.chat.infrastructure.adapters.in.websocket.ChatWebSocketHandler;
import com.example.chatConnectSpring.chat.infrastructure.mapper.ChatResponseMapper;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    private final CreateChatUseCase createChatUseCase;
    private final FindChatByIdUseCase findChatByIdUseCase;
    private final FindChatsByUserIdUseCase findChatsByUserIdUseCase;
    private final AddParticipantUseCase addParticipantUseCase;
    private final RemoveParticipantUseCase removeParticipantUseCase;
    private final UpdateTitleChatUseCase updateTitleChatUseCase;
    private final ChatWebSocketHandler chatWebSocketHandler;

    public ChatController(
            CreateChatUseCase createChatUseCase,
            FindChatByIdUseCase findChatByIdUseCase,
            FindChatsByUserIdUseCase findChatsByUserIdUseCase,
            AddParticipantUseCase addParticipantUseCase,
            RemoveParticipantUseCase removeParticipantUseCase,
            UpdateTitleChatUseCase updateTitleChatUseCase,
            ChatWebSocketHandler chatWebSocketHandler
    ) {
        this.createChatUseCase = createChatUseCase;
        this.findChatByIdUseCase = findChatByIdUseCase;
        this.findChatsByUserIdUseCase = findChatsByUserIdUseCase;
        this.addParticipantUseCase = addParticipantUseCase;
        this.removeParticipantUseCase = removeParticipantUseCase;
        this.updateTitleChatUseCase = updateTitleChatUseCase;
        this.chatWebSocketHandler = chatWebSocketHandler;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ChatResponseDTO create(@Valid @RequestBody CreateChatRequestDTO request) {
        var chat = createChatUseCase.create(new CreateChatCommand(
                request.createdByUserId(),
                request.title(),
                request.type()
        ));
        chatWebSocketHandler.broadcastChatCreated(chat);
        return ChatResponseMapper.toDTO(chat);
    }

    @GetMapping("/{chatId}")
    public ChatResponseDTO findById(@PathVariable UUID chatId) {
        return ChatResponseMapper.toDTO(findChatByIdUseCase.findById(chatId));
    }

    @GetMapping
    public Page<ChatResponseDTO> findByUserId(
            @RequestParam UUID userId,
            Pageable pageable
    ) {
        return findChatsByUserIdUseCase.findChatsByUserId(userId, pageable)
                .map(ChatResponseMapper::toDTO);
    }

    @PostMapping("/{chatId}/participants")
    @ResponseStatus(HttpStatus.CREATED)
    public ChatParticipantResponseDTO addParticipant(
            @PathVariable UUID chatId,
            @RequestParam UUID userId,
            @Valid @RequestBody AddParticipantRequestDTO request
    ) {
        return ChatResponseMapper.toDTO(
                addParticipantUseCase.addParticipant(new AddParticipantCommand(chatId, userId, request.userId()))
        );
    }

    @PutMapping("/{chatId}/title")
    public ChatResponseDTO updateTitle(
            @PathVariable UUID chatId,
            @Valid @RequestBody UpdateTitleChatRequestDTO request
    ) {
        return ChatResponseMapper.toDTO(
                updateTitleChatUseCase.updateTitle(new UpdateTitleChatCommand(
                        chatId,
                        request.userId(),
                        request.newTitle()
                ))
        );
    }

    @DeleteMapping("/{chatId}/participants/{participantId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeParticipant(
            @PathVariable UUID chatId,
            @PathVariable UUID participantId,
            @RequestParam UUID userId
    ) {
        removeParticipantUseCase.removeParticipant(new RemoveParticipantCommand(chatId, userId, participantId));
    }
}
