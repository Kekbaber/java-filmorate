package ru.yandex.practicum.filmorate.storage.inmemory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Profile("inmemory")
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
        log.info("Storage: remove friendship {} <-> {}", userId, friendId);
        friendships.remove(f1);
        friendships.remove(f2);
    }
}
