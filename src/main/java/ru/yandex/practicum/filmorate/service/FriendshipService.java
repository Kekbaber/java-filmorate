package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface FriendshipService {
    public void add(long userId, long friendId);

    public void remove(long userId, long friendId);

    public Collection<User> get(long userId);

    public Collection<User> getCommonFriends(long id, long otherId);

    public boolean areFriends(long userId, long friendId);
}
