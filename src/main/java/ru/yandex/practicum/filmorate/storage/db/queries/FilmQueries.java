package ru.yandex.practicum.filmorate.storage.db.queries;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FilmQueries {

    public static final String FIND_ALL = "SELECT * FROM films";

    public static final String FIND_BY_ID = "SELECT * FROM films WHERE id = ?";

    public static final String INSERT_FILM =
            "INSERT INTO films(name, description, release_date, duration, rating_id) VALUES(?, ?, ?, ?, ?)";

    public static final String UPDATE_FILM =
            "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? WHERE id = ?";

    public static final String DELETE_FILM = "DELETE FROM films WHERE id = ?";

    public static final String FIND_POPULAR_FILMS =
            "SELECT f.* FROM films f LEFT JOIN likes l ON f.id = l.film_id GROUP BY f.id ORDER BY COUNT(l.user_id) DESC LIMIT ?";
}
