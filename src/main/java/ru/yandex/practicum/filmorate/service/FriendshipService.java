package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.impl.InMemoryFriendshipStorage;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FriendshipService {

    private final FriendshipStorage storage;
    private final UserService userService;

    public FriendshipService(InMemoryFriendshipStorage storage,
                             UserService userService) {
        this.storage = storage;
        this.userService = userService;
    }

    public void add(long userId, long friendId) {
        log.info("Creating new friendship between {} and {}", userId, friendId);
        userService.findById(userId);
        userService.findById(friendId);
        storage.add(userId, friendId);
    }

    public void remove(long userId, long friendId) {
        log.info("Removing friendship between {} and {}", userId, friendId);
        userService.findById(userId);
        userService.findById(friendId);
        storage.remove(userId, friendId);
    }

    public Collection<User> get(long userId) {
        log.info("Get friends of {}", userId);
        userService.findById(userId);
        return storage.findById(userId).stream()
                .map(userService::findById)
                .collect(Collectors.toList());
    }

    public Collection<User> getCommonFriends(long id, long otherId) {
        log.info("Get common friends between {} and {}", id, otherId);
        userService.findById(id);
        userService.findById(otherId);

        Set<Long> ids = storage.findById(id);
        Set<Long> otherIds = storage.findById(otherId);

        return ids.stream()
                .filter(otherIds::contains)
                .map(userService::findById)
                .toList();
    }
}
