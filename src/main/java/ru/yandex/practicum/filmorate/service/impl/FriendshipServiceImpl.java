package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dto.response.UserResponse;
import ru.yandex.practicum.filmorate.exception.model.DuplicateFriendshipException;
import ru.yandex.practicum.filmorate.exception.model.NotFoundException;
import ru.yandex.practicum.filmorate.service.FriendshipService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FriendshipServiceImpl implements FriendshipService {

    private final FriendshipStorage friendshipStorage;
    private final UserService userService;

    @Override
    @Transactional
    public void addFriendRequest(long userId, long friendId) {
        log.info("Creating new friendship between {} and {}", userId, friendId);
        userService.findById(userId);
        userService.findById(friendId);
        if (friendshipStorage.findConfirmedFriendIds(userId).contains(friendId)) {
            throw new DuplicateFriendshipException("Вы уже друзья");
        }
        friendshipStorage.addFriendRequest(userId, friendId, true);
        log.info("Пользователь {} подписан на {}", userId, friendId);
    }

    @Override
    @Transactional
    public void confirmFriendRequest(long userId, long friendId) {
        userService.findById(userId);
        userService.findById(friendId);
        if (!friendshipStorage.findIncomingRequests(userId).contains(friendId)) {
            throw new NotFoundException("Нет входящей заявки от пользователя " + friendId);
        }
        friendshipStorage.deleteFriendship(friendId, userId);
        friendshipStorage.addFriendRequest(userId, friendId, true);
        friendshipStorage.addFriendRequest(friendId, userId, true);
        log.info("Пользователь {} подтвердил дружбу с {}", userId, friendId);
    }

    @Override
    @Transactional
    public void deleteFriend(long userId, long friendId) {
        log.debug("Removing friendship between {} and {}", userId, friendId);
        userService.findById(userId);
        userService.findById(friendId);
        friendshipStorage.deleteFriendship(userId, friendId);
        log.debug("Friendship removed from storage for {} <-> {}", userId, friendId);
    }

    @Override
    public List<UserResponse> findOutgoingRequests(long userId) {
        userService.findById(userId);
        Set<Long> requestIds = friendshipStorage.findOutgoingRequests(userId);
        log.debug("Returned {} friends for user {}", requestIds.size(), userId);
        return userService.findAllByIds(requestIds);
    }

    @Override
    public List<UserResponse> findIncomingRequests(long userId) {
        userService.findById(userId);
        Set<Long> requestIds = friendshipStorage.findIncomingRequests(userId);
        return userService.findAllByIds(requestIds);
    }

    @Override
    public List<UserResponse> findConfirmedFriends(long userId) {
        userService.findById(userId);
        Set<Long> friendIds = friendshipStorage.findConfirmedFriendIds(userId);
        return userService.findAllByIds(friendIds);
    }

    @Override
    public List<UserResponse> findCommonFriends(long userId, long otherId) {
        log.info("Get common friends between users {} and {}", userId, otherId);
        userService.findById(userId);
        userService.findById(otherId);
        Set<Long> userIds = friendshipStorage.findConfirmedFriendIds(userId);
        Set<Long> otherIds = friendshipStorage.findConfirmedFriendIds(otherId);
        log.debug("Friends of {}: {}, friends of {}: {}", userId, userIds, otherId, otherIds);
        Set<Long> commonIds = userIds.stream()
                .filter(otherIds::contains)
                .collect(Collectors.toSet());
        log.debug("Found {} common friends", commonIds.size());
        return userService.findAllByIds(commonIds);
    }
}
