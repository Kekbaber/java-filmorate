package ru.yandex.practicum.filmorate.storage.db.queries;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GenreQueries {

    public static final String FIND_ALL = "SELECT * FROM genres";

    public static final String FIND_BY_ID = "SELECT * FROM genres WHERE id = ?";

    public static final String FIND_BY_FILM_ID =
            "SELECT g.id, g.name FROM genres g JOIN film_genre fg ON g.id = fg.genre_id WHERE fg.film_id = ? ORDER BY g.id";

    public static final String INSERT_FILM_GENRE = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";

    public static final String DELETE_FILM_GENRES = "DELETE FROM film_genre WHERE film_id = ?";
}