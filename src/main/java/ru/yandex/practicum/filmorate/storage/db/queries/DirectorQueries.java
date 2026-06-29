package ru.yandex.practicum.filmorate.storage.db.queries;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DirectorQueries {

    public static final String FIND_ALL = "SELECT * FROM directors";

    public static final String FIND_BY_ID = "SELECT * FROM directors WHERE id = ?";

    public static final String INSERT_DIRECTOR = "INSERT INTO directors(name) VALUES (?)";

    public static final String UPDATE_DIRECTOR = "UPDATE directors SET name = ? WHERE id = ?";

    public static final String DELETE_DIRECTOR = "DELETE FROM directors WHERE id = ?";

    public static final String FIND_EXISTING_IDS = "SELECT id FROM directors WHERE id IN (:ids)";

    public static final String FIND_BY_FILM_IDS = """
            SELECT df.film_id, d.id, d.name
            FROM director_films df
            JOIN directors AS d ON df.director_id=d.id
            WHERE df.film_id IN (:ids)
            """;

    public static final String ADD_DIRECTOR_TO_FILM = "INSERT INTO director_films(film_id, director_id) VALUES (?, ?)";

}
