package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryFriendshipStorage implements FriendshipStorage {

    private final Set<Friendship> friendships = new HashSet<>();

    @Override
    public Set<Long> findById(long userId) {
        log.debug("Find friends of user id={} in storage", userId);
        return friendships.stream()
                .filter(f -> f.getUserId() == userId)
                .map(Friendship::getFriendId)
                .collect(Collectors.toSet());
    }

    @Override
    public void add(long userId, long friendId) {
        log.info("Storage: add friendship {} <-> {}", userId, friendId);
        friendships.add(new Friendship(userId, friendId));
        friendships.add(new Friendship(friendId, userId));
        log.trace("Current friendships size = {}", friendships.size());
    }

    @Override
    public void remove(long userId, long friendId) {
        Friendship f1 = new Friendship(userId, friendId);
        Friendship f2 = new Friendship(friendId, userId);
        if (!friendships.contains(f1) && !friendships.contains(f2)) {
            log.warn("Attempt to remove non-existing friendship {} <-> {}", userId, friendId);
            throw new NotFoundException("Не найдено дружбы между " + userId + " и " + friendId);
        }
        log.info("Storage: remove friendship {} <-> {}", userId, friendId);
        friendships.remove(f1);
        friendships.remove(f2);
    }
}
