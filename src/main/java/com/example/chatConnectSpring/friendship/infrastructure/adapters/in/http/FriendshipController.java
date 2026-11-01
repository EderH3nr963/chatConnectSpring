package com.example.chatConnectSpring.friendship.infrastructure.adapters.in.http;

import com.example.chatConnectSpring.friendship.application.service.OnlineFriendsService;
import com.example.chatConnectSpring.friendship.domain.model.friendShip.FriendShip;
import com.example.chatConnectSpring.friendship.domain.model.userblock.UserBlock;
import com.example.chatConnectSpring.friendship.domain.ports.in.FriendShipUseCase;
import com.example.chatConnectSpring.friendship.domain.ports.in.UserBlockUseCase;
import com.example.chatConnectSpring.friendship.infrastructure.adapters.in.http.dto.request.InviteFriendRequestDTO;
import com.example.chatConnectSpring.friendship.infrastructure.adapters.in.http.dto.response.FriendResponseDTO;
import com.example.chatConnectSpring.friendship.infrastructure.adapters.in.http.dto.response.FriendshipResponseDTO;
import com.example.chatConnectSpring.friendship.infrastructure.adapters.in.http.dto.response.UserBlockDTO;
import com.example.chatConnectSpring.friendship.infrastructure.mapper.FriendShipMapper;
import com.example.chatConnectSpring.friendship.infrastructure.mapper.UserBlockMapper;
import com.example.chatConnectSpring.auth.infrastructure.security.UserDetailsImpl;
import com.example.chatConnectSpring.user.domain.model.User;
import com.example.chatConnectSpring.user.domain.port.in.UserUseCase;
import com.example.chatConnectSpring.user.domain.port.out.UserStoragePort;
import io.swagger.v3.oas.annotations.Operation;
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
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.Objects;
import java.util.UUID;

@Tag(name = "Friendships", description = "Operações de gerenciamento de amizades, solicitações e bloqueios")
@RestController
@RequestMapping("/api/friendships")
public class FriendshipController {

    private static final Logger log = LoggerFactory.getLogger(FriendshipController.class);

    private final FriendShipUseCase friendShipUseCase;
    private final UserBlockUseCase userBlockUseCase;
    private final UserUseCase userUseCase;
    private final OnlineFriendsService onlineFriendsService;
    private final UserStoragePort userStoragePort;

    public FriendshipController(FriendShipUseCase friendShipUseCase, UserBlockUseCase userBlockUseCase, UserUseCase userUseCase, OnlineFriendsService onlineFriendsService, UserStoragePort userStoragePort) {
        this.friendShipUseCase = friendShipUseCase;
        this.userBlockUseCase = userBlockUseCase;
        this.userUseCase = userUseCase;
        this.onlineFriendsService = onlineFriendsService;
        this.userStoragePort = userStoragePort;
    }
    
    @PostMapping("/invite")
    @Operation(summary = "Enviar solicitação de amizade")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Solicitação enviada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "Amizade ou solicitação já existente")
    })
    public ResponseEntity<FriendshipResponseDTO> invite(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody InviteFriendRequestDTO dto
    ) {
        log.info("HTTP POST /api/friendships/invite userId={} targetId={}", userDetails.getId(), dto.targetUserId());
        FriendShip friendship = friendShipUseCase.invite(userDetails.getId(), dto.targetUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(FriendShipMapper.toDTO(friendship, userStoragePort));
    }

    @PostMapping("/accept/{requesterId}")
    @Operation(summary = "Aceitar solicitação de amizade")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Solicitação aceita com sucesso"),
            @ApiResponse(responseCode = "404", description = "Solicitação não encontrada"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para aceitar este convite")
    })
    public ResponseEntity<FriendshipResponseDTO> acceptInvite(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID requesterId
    ) {
        log.info("HTTP POST /api/friendships/accept/{} by userId={}", requesterId, userDetails.getId());
        FriendShip friendship = friendShipUseCase.acceptInvite(userDetails.getId(), requesterId);
        return ResponseEntity.ok(FriendShipMapper.toDTO(friendship, userStoragePort));
    }

    @PostMapping("/reject/{requesterId}")
    @Operation(summary = "Rejeitar solicitação de amizade")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Solicitação rejeitada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Solicitação não encontrada"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para rejeitar este convite")
    })
    public ResponseEntity<Void> rejectInvite(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID requesterId
    ) {
        log.info("HTTP POST /api/friendships/reject/{} by userId={}", requesterId, userDetails.getId());
        friendShipUseCase.rejectInvite(userDetails.getId(), requesterId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/cancel/{targetUserId}")
    @Operation(summary = "Cancelar solicitação de amizade enviada")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Solicitação cancelada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Solicitação não encontrada"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para cancelar este convite")
    })
    public ResponseEntity<Void> cancelInvite(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID targetUserId
    ) {
        log.info("HTTP POST /api/friendships/cancel/{} by userId={}", targetUserId, userDetails.getId());
        friendShipUseCase.cancelInvite(userDetails.getId(), targetUserId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{friendId}")
    @Operation(summary = "Desfazer amizade")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Amizade desfeita com sucesso"),
            @ApiResponse(responseCode = "404", description = "Amizade não encontrada")
    })
    public ResponseEntity<Void> removeFriend(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID friendId
    ) {
        log.info("HTTP DELETE /api/friendships/{} by userId={}", friendId, userDetails.getId());
        friendShipUseCase.removeFriend(userDetails.getId(), friendId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/block/{targetUserId}")
    @Operation(summary = "Bloquear usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuário bloqueado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<Void> blockUser(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID targetUserId
    ) {
        log.info("HTTP POST /api/friendships/block/{} by userId={}", targetUserId, userDetails.getId());
        userBlockUseCase.blockUser(userDetails.getId(), targetUserId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/unblock/{targetUserId}")
    @Operation(summary = "Desbloquear usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuário desbloqueado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Bloqueio não encontrado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para desbloquear")
    })
    public ResponseEntity<Void> unblockUser(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID targetUserId
    ) {
        log.info("HTTP POST /api/friendships/unblock/{} by userId={}", targetUserId, userDetails.getId());
        userBlockUseCase.unblockUser(userDetails.getId(), targetUserId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Listar amigos do usuário autenticado")
    public ResponseEntity<List<FriendResponseDTO>> listFriends(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        UUID currentUserId = userDetails.getId();
        List<FriendShip> friendships = friendShipUseCase.listFriends(currentUserId);
        if (friendships.isEmpty()) return ResponseEntity.ok(List.of());

        List<UUID> friendIds = friendships.stream()
                .map(fs -> fs.getOtherUserId(currentUserId))
                .filter(Objects::nonNull)
                .toList();

        Map<UUID, User> userById = userUseCase.findUsersByUsersId(friendIds).stream()
                .filter(u -> u.getId() != null)
                .collect(Collectors.toMap(User::getId, Function.identity(), (a, b) -> a));

        List<FriendResponseDTO> friends = friendships.stream()
                .map(fs -> {
                    User friendUser = userById.get(fs.getOtherUserId(currentUserId));
                    return friendUser != null ? FriendShipMapper.toFriendDTO(fs, friendUser, userStoragePort) : null;
                })
                .filter(Objects::nonNull)
                .toList();
        return ResponseEntity.ok(friends);
    }

    @GetMapping("/pending/received")
    @Operation(summary = "Listar convites de amizade recebidos e pendentes")
    public ResponseEntity<List<FriendshipResponseDTO>> listPendingReceived(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        List<FriendShip> pending = friendShipUseCase.listPendingReceivedInvites(userDetails.getId());
        return ResponseEntity.ok(pending.stream().map(fs -> FriendShipMapper.toDTO(fs, userStoragePort)).toList());
    }

    @GetMapping("/pending/sent")
    @Operation(summary = "Listar convites de amizade enviados e pendentes")
    public ResponseEntity<List<FriendshipResponseDTO>> listPendingSent(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        List<FriendShip> pending = friendShipUseCase.listPendingSentInvites(userDetails.getId());
        return ResponseEntity.ok(pending.stream().map(fs -> FriendShipMapper.toDTO(fs, userStoragePort)).toList());
    }

    @GetMapping("/blocked")
    @Operation(summary = "Listar usuários bloqueados pelo usuário autenticado")
    public ResponseEntity<List<UserBlockDTO>> listBlocked(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        List<UserBlock> blocked = userBlockUseCase.listBlockedUsers(userDetails.getId());
        return ResponseEntity.ok(blocked.stream().map(UserBlockMapper::toDTO).toList());
    }

    @GetMapping("/status/{targetUserId}")
    @Operation(summary = "Consultar status de amizade com outro usuário")
    public ResponseEntity<FriendshipResponseDTO> getStatus(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID targetUserId
    ) {
        FriendShip friendship = friendShipUseCase.findFriendShip(userDetails.getId(), targetUserId);
        return ResponseEntity.ok(FriendShipMapper.toDTO(friendship, userStoragePort));
    }

    @GetMapping("/online")
    @Operation(summary = "Listar amigos online do usuário autenticado")
    public ResponseEntity<List<FriendResponseDTO>> listOnline(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return ResponseEntity.ok(onlineFriendsService.listOnline(userDetails.getId()));
    }
}
