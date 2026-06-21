package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.dto.response.UserResponse;

import java.util.List;

public interface FriendshipService {
    List<UserResponse> findConfirmedFriends(long userId);

    List<UserResponse> findCommonFriends(long userId, long otherId);

    void addFriendRequest(long userId, long friendId);

    void confirmFriendRequest(long userId, long friendId);

    void deleteFriend(long userId, long friendId);

    List<UserResponse> findOutgoingRequests(long userId);

    List<UserResponse> findIncomingRequests(long userId);
}
