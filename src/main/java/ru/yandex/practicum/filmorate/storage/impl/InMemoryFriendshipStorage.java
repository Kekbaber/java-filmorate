package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
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
        log.info("Find friendship by id={}", userId);
        return friendships.stream()
                .filter(f -> f.getUserId() == userId)
                .map(Friendship::getFriendId)
                .collect(Collectors.toSet());
    }

    @Override
    public void add(long userId, long friendId) {
        log.info("Add friendship: {} <-> {}", userId, friendId);
        friendships.add(new Friendship(userId, friendId));
        friendships.add(new Friendship(friendId, userId));
    }

    @Override
    public void remove(long userId, long friendId) {
        log.info("Remove friendship: {} <-> {}", userId, friendId);
        friendships.remove(new Friendship(userId, friendId));
        friendships.remove(new Friendship(friendId, userId));
    }
}
