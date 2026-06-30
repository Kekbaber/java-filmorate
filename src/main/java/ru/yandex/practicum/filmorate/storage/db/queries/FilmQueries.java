package ru.yandex.practicum.filmorate.storage.db.queries;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FilmQueries {

    public static final String FIND_ALL = """
            SELECT f.*, r.name AS rating_name
            FROM films f
            LEFT JOIN ratings r ON f.rating_id = r.id
            """;

    public static final String FIND_BY_ID = """
            SELECT f.*, r.name AS rating_name
            FROM films f
            LEFT JOIN ratings r ON f.rating_id = r.id
            WHERE f.id = ?
            """;

    public static final String INSERT_FILM = """
            INSERT INTO films(name, description, release_date, duration, rating_id)
            VALUES(?, ?, ?, ?, ?)
            """;

    public static final String UPDATE_FILM = """
            UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? WHERE id = ?
            """;

    public static final String DELETE_FILM = "DELETE FROM films WHERE id = ?";

    public static final String FIND_POPULAR_FILMS = """
            SELECT f.*, r.name AS rating_name,
                   (SELECT COUNT(*) FROM likes l WHERE l.film_id = f.id) AS likes_count
            FROM films f
            LEFT JOIN ratings r ON f.rating_id = r.id
            WHERE (? IS NULL OR EXISTS (SELECT 1 FROM film_genre fg WHERE fg.film_id = f.id AND fg.genre_id = ?))
              AND (? IS NULL OR EXTRACT(YEAR FROM f.release_date) = ?)
            ORDER BY likes_count DESC
            LIMIT ?
            """;

    public static final String FIND_COMMON_FILMS = """
            SELECT f.*, r.name AS rating_name
            FROM films f
            LEFT JOIN ratings r ON f.rating_id = r.id
            WHERE EXISTS (SELECT 1 FROM likes WHERE film_id = f.id AND user_id = ?)
              AND EXISTS (SELECT 1 FROM likes WHERE film_id = f.id AND user_id = ?)
            ORDER BY (SELECT COUNT(*) FROM likes WHERE film_id = f.id) DESC
            """;
}
