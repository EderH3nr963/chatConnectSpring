package com.example.chatConnectSpring.friendship.application.service;

import com.example.chatConnectSpring.friendship.domain.model.friendShip.FriendShip;
import com.example.chatConnectSpring.friendship.domain.ports.out.FriendShipRepository;
import com.example.chatConnectSpring.friendship.infrastructure.adapters.in.http.dto.response.FriendResponseDTO;
import com.example.chatConnectSpring.friendship.infrastructure.mapper.FriendShipMapper;
import com.example.chatConnectSpring.presence.domain.ports.out.PresenceRepository;
import com.example.chatConnectSpring.user.domain.model.User;
import com.example.chatConnectSpring.user.domain.port.in.UserUseCase;
import com.example.chatConnectSpring.user.domain.port.out.UserStoragePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class OnlineFriendsService {

    private final FriendShipRepository friendShipRepository;
    private final PresenceRepository presenceRepository;
    private final UserUseCase userUseCase;
    private final UserStoragePort userStoragePort;

    public OnlineFriendsService(FriendShipRepository friendShipRepository,
                                PresenceRepository presenceRepository,
                                UserUseCase userUseCase,
                                UserStoragePort userStoragePort) {
        this.friendShipRepository = friendShipRepository;
        this.presenceRepository = presenceRepository;
        this.userUseCase = userUseCase;
        this.userStoragePort = userStoragePort;
    }

    /**
     * Lista amigos online de forma otimizada:
     * 1. Busca friendships aceitas (1 query)
     * 2. Filtra online via Redis (pipeline / set)
     * 3. Busca Users em lote via IN (1 query) - elimina N+1
     */
    @Transactional(readOnly = true)
    public List<FriendResponseDTO> listOnline(UUID userId) {
        if (userId == null) return List.of();

        List<FriendShip> friendships = friendShipRepository.findAcceptedFriends(userId);
        if (friendships.isEmpty()) return List.of();

        List<UUID> friendIds = friendships.stream()
                .map(f -> f.getOtherUserId(userId))
                .filter(Objects::nonNull)
                .toList();
        if (friendIds.isEmpty()) return List.of();

        Set<UUID> onlineIds = presenceRepository.filterOnline(friendIds);
        if (onlineIds.isEmpty()) return List.of();

        List<FriendShip> onlineFriendships = friendships.stream()
                .filter(f -> onlineIds.contains(f.getOtherUserId(userId)))
                .toList();
        if (onlineFriendships.isEmpty()) return List.of();

        List<UUID> onlineFriendIds = onlineFriendships.stream()
                .map(f -> f.getOtherUserId(userId))
                .toList();

        List<User> users = userUseCase.findUsersByUsersId(onlineFriendIds);
        Map<UUID, User> userById = users.stream()
                .filter(u -> u.getId() != null)
                .collect(Collectors.toMap(User::getId, Function.identity(), (a, b) -> a));

        return onlineFriendships.stream()
                .map(fs -> {
                    UUID friendId = fs.getOtherUserId(userId);
                    User friendUser = userById.get(friendId);
                    return friendUser != null ? FriendShipMapper.toFriendDTO(fs, friendUser, userStoragePort) : null;
                })
                .filter(Objects::nonNull)
                .toList();
    }
}
