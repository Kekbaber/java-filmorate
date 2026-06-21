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
    public Set<Long> findConfirmedFriendIds(long userId) {
        log.debug("Find confirmed friends of user id={} in storage", userId);
        return friendships.stream()
                .filter(f -> f.getUserId() == userId && f.isConfirmed())
                .map(Friendship::getFriendId)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Long> findOutgoingRequests(long userId) {
        log.debug("Find outgoing requests of user id={} in storage", userId);
        return friendships.stream()
                .filter(f -> f.getUserId() == userId && !f.isConfirmed())
                .map(Friendship::getFriendId)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Long> findIncomingRequests(long userId) {
        log.debug("Find incoming requests for user id={} in storage", userId);
        return friendships.stream()
                .filter(f -> f.getFriendId() == userId && !f.isConfirmed())
                .map(Friendship::getUserId)
                .collect(Collectors.toSet());
    }

    @Override
    public void addFriendRequest(long userId, long friendId, boolean confirmed) {
        log.debug("Storage: add request {} -> {} confirmed={}", userId, friendId, confirmed);
        // Удаляем возможную предыдущую запись с таким же направлением
        friendships.removeIf(f -> f.getUserId() == userId && f.getFriendId() == friendId);
        friendships.add(new Friendship(userId, friendId, confirmed));
        log.debug("Current friendships size = {}", friendships.size());
    }

    @Override
    public void deleteFriendship(long userId, long friendId) {
        log.debug("Storage: remove all relations between {} and {}", userId, friendId);
        // Удаляем записи в обе стороны
        friendships.removeIf(f -> (f.getUserId() == userId && f.getFriendId() == friendId) ||
                (f.getUserId() == friendId && f.getFriendId() == userId));
        log.debug("Current friendships size = {}", friendships.size());
    }

    @Override
    public Set<Long> findCommonFriendIds(long userId, long otherId) {
        log.debug("Find common friends of userId={} and otherId={} in storage", userId, otherId);
        Set<Long> userFriends = findConfirmedFriendIds(userId);
        Set<Long> otherFriends = findConfirmedFriendIds(otherId);
        userFriends.retainAll(otherFriends);
        return userFriends;
    }
}
