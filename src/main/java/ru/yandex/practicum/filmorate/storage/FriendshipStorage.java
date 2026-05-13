package ru.yandex.practicum.filmorate.storage;

import java.util.Set;

public interface FriendshipStorage {
    public Set<Long> findById(long userId);

    public void add(long userId, long friendId);

    public void remove(long userId, long friendId);
}
