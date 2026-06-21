package ru.yandex.practicum.filmorate.storage;

import java.util.Set;

public interface FriendshipStorage {
    Set<Long> findConfirmedFriendIds(long userId);

    Set<Long> findOutgoingRequests(long userId);

    Set<Long> findIncomingRequests(long userId);

    void addFriendRequest(long userId, long friendId, boolean confirmed);

    void deleteFriendship(long userId, long friendId);

    Set<Long> findCommonFriendIds(long userId, long otherId);
}