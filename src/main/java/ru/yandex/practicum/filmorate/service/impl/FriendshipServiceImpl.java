package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dto.response.UserResponse;
import ru.yandex.practicum.filmorate.exception.model.DuplicateFriendshipException;
import ru.yandex.practicum.filmorate.exception.model.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.service.EventService;
import ru.yandex.practicum.filmorate.service.FriendshipService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;

import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FriendshipServiceImpl implements FriendshipService {

    private final FriendshipStorage friendshipStorage;
    private final UserService userService;
    private final EventService eventService;

    @Override
    @Transactional
    public void addFriendRequest(long userId, long friendId) {
        log.debug("Add friend request: userId={}, friendId={}", userId, friendId);
        userService.findById(userId);
        userService.findById(friendId);
        if (friendshipStorage.findConfirmedFriendIds(userId).contains(friendId)) {
            throw new DuplicateFriendshipException("Вы уже друзья");
        }
        friendshipStorage.addFriendRequest(userId, friendId, true);
        eventService.save(Event.of(userId, friendId, EventType.FRIEND, EventOperation.ADD));
        log.debug("Friend request added: userId={}, friendId={}", userId, friendId);
    }

    @Override
    @Transactional
    public void confirmFriendRequest(long userId, long friendId) {
        log.debug("Confirm friendship: userId={}, friendId={}", userId, friendId);
        userService.findById(userId);
        userService.findById(friendId);
        if (!friendshipStorage.findIncomingRequests(userId).contains(friendId)) {
            throw new NotFoundException("Нет входящей заявки от пользователя " + friendId);
        }
        friendshipStorage.deleteFriendship(friendId, userId);
        friendshipStorage.addFriendRequest(userId, friendId, true);
        log.debug("Friendship confirmed: userId={}, friendId={}", userId, friendId);
    }

    @Override
    @Transactional
    public void deleteFriend(long userId, long friendId) {
        log.debug("Delete friend: userId={}, friendId={}", userId, friendId);
        userService.findById(userId);
        userService.findById(friendId);
        friendshipStorage.deleteFriendship(userId, friendId);
        eventService.save(Event.of(userId, friendId, EventType.FRIEND, EventOperation.REMOVE));
        log.debug("Friend deleted: userId={}, friendId={}", userId, friendId);
    }

    @Override
    public List<UserResponse> findOutgoingRequests(long userId) {
        userService.findById(userId);
        Set<Long> requestIds = friendshipStorage.findOutgoingRequests(userId);
        log.debug("Found {} outgoing requests for userId={}", requestIds.size(), userId);
        return userService.findAllByIds(requestIds);
    }

    @Override
    public List<UserResponse> findIncomingRequests(long userId) {
        log.debug("Get incoming requests for userId={}", userId);
        userService.findById(userId);
        Set<Long> requestIds = friendshipStorage.findIncomingRequests(userId);
        log.debug("Found {} incoming requests for userId={}", requestIds.size(), userId);
        return userService.findAllByIds(requestIds);
    }

    @Override
    public List<UserResponse> findConfirmedFriends(long userId) {
        log.debug("Get confirmed friends for userId={}", userId);
        userService.findById(userId);
        Set<Long> friendIds = friendshipStorage.findConfirmedFriendIds(userId);
        log.debug("Found {} confirmed friends for userId={}", friendIds.size(), userId);
        return userService.findAllByIds(friendIds);
    }

    @Override
    public List<UserResponse> findCommonFriends(long userId, long otherId) {
        log.debug("Get common friends: userId={}, otherId={}", userId, otherId);
        userService.findById(userId);
        userService.findById(otherId);
        Set<Long> commonIds = friendshipStorage.findCommonFriendIds(userId, otherId);
        eventService.save(Event.of(userId, otherId, EventType.FRIEND, EventOperation.UPDATE));
        log.debug("Found {} common friends", commonIds.size());
        return userService.findAllByIds(commonIds);
    }
}
