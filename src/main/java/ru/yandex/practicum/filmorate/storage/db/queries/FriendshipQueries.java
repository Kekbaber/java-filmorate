package ru.yandex.practicum.filmorate.storage.db.queries;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FriendshipQueries {

    public static final String FIND_CONFIRMED_FRIENDS =
            "SELECT friend_id FROM friendships WHERE user_id = ? AND confirmed = TRUE";

    public static final String FIND_OUTGOING_REQUESTS =
            "SELECT friend_id FROM friendships WHERE user_id = ? AND confirmed = FALSE";

    public static final String FIND_INCOMING_REQUESTS =
            "SELECT user_id FROM friendships WHERE friend_id = ? AND confirmed = FALSE";

    public static final String ADD_REQUEST =
            "MERGE INTO friendships (user_id, friend_id, confirmed) VALUES (?, ?, ?)";

    public static final String REMOVE_FRIENDSHIP =
            "DELETE FROM friendships WHERE user_id = ? AND friend_id = ?";
}
