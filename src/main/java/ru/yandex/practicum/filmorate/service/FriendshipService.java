package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FriendshipService {

    private final FriendshipStorage storage;
    private final UserService userService;

    public void add(long userId, long friendId) {
        log.info("Creating new friendship between {} and {}", userId, friendId);
        log.debug("Checking existence of users {} and {}", userId, friendId);
        userService.findById(userId);
        userService.findById(friendId);
        storage.add(userId, friendId);
        log.debug("Friendship added to storage for {} <-> {}", userId, friendId);
    }

    public void remove(long userId, long friendId) {
        log.info("Removing friendship between {} and {}", userId, friendId);
        log.debug("Checking existence of users {} and {}", userId, friendId);
        userService.findById(userId);
        userService.findById(friendId);
        storage.remove(userId, friendId);
        log.debug("Friendship removed from storage for {} <-> {}", userId, friendId);
    }

    public Collection<User> get(long userId) {
        log.info("Get friends of user id={}", userId);
        log.debug("Validating user id={} existence", userId);
        userService.findById(userId);
        Set<Long> friendIds = storage.findById(userId);
        log.debug("Found friend ids: {}", friendIds);
        Collection<User> friends = friendIds.stream()
                .map(userService::findById)
                .collect(Collectors.toList());
        log.debug("Returned {} friends for user {}", friends.size(), userId);
        return friends;
    }

    public Collection<User> getCommonFriends(long id, long otherId) {
        log.info("Get common friends between users {} and {}", id, otherId);
        log.debug("Validating existence of users {} and {}", id, otherId);
        userService.findById(id);
        userService.findById(otherId);

        Set<Long> ids = storage.findById(id);
        Set<Long> otherIds = storage.findById(otherId);
        log.debug("Friends of {}: {}, friends of {}: {}", id, ids, otherId, otherIds);

        Collection<User> common = ids.stream()
                .filter(otherIds::contains)
                .map(userService::findById)
                .toList();
        log.debug("Found {} common friends", common.size());
        return common;
    }
}
