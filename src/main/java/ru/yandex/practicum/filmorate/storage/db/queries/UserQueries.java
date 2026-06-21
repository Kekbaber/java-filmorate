package ru.yandex.practicum.filmorate.storage.db.queries;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserQueries {

    public static final String FIND_ALL_USERS = "SELECT * FROM users";

    public static final String FIND_USER_BY_ID = "SELECT * FROM users WHERE id = ?";

    public static final String FIND_USERS_BY_IDS = "SELECT * FROM users WHERE id IN (:ids)";

    public static final String INSERT_USER = "INSERT INTO users(email, login, name, birthday) VALUES (?, ?, ?, ?)";

    public static final String UPDATE_USER = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";

    public static final String DELETE_USER = "DELETE FROM users WHERE id = ?";
}
