package ru.yandex.practicum.filmorate.storage.db.queries;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LikeQueries {

    public static final String FIND_BY_ID = "SELECT * FROM likes WHERE film_id = ?";

    public static final String INSERT_LIKE = "INSERT INTO likes(film_id, user_id) VALUES(?, ?)";

    public static final String DELETE_LIKE = "DELETE FROM likes WHERE film_id = ? and user_id = ?";

    public static final String FIND_MOST_LIKED_FILMS_IDS =
            "SELECT film_id, COUNT(*) as like_count FROM likes GROUP BY film_id ORDER BY like_count DESC, film_id ASC LIMIT ?";

}
