package ru.yandex.practicum.filmorate.storage.db.queries;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ReviewQueries {

    public static final String FIND_BY_ID = """
            SELECT r.id, r.content, r.is_positive, r.user_id, r.film_id,
            COALESCE(SUM(CASE WHEN rl.is_like THEN 1 WHEN NOT rl.is_like THEN -1 ELSE 0 END), 0) AS useful
            FROM reviews r
            LEFT JOIN review_likes rl ON r.id = rl.review_id
            WHERE r.id = ?
            GROUP BY r.id
            """;

    public static final String FIND_ALL = """
            SELECT r.id, r.content, r.is_positive, r.user_id, r.film_id,
            COALESCE(SUM(CASE WHEN rl.is_like THEN 1 WHEN NOT rl.is_like THEN -1 ELSE 0 END), 0) AS useful
            FROM reviews r
            LEFT JOIN review_likes rl ON r.id = rl.review_id
            GROUP BY r.id
            ORDER BY useful DESC
            LIMIT ?
            """;

    public static final String FIND_BY_FILM_ID = """
            SELECT r.id, r.content, r.is_positive, r.user_id, r.film_id,
            COALESCE(SUM(CASE WHEN rl.is_like THEN 1 WHEN NOT rl.is_like THEN -1 ELSE 0 END), 0) AS useful
            FROM reviews r
            LEFT JOIN review_likes rl ON r.id = rl.review_id
            WHERE r.film_id = ?
            GROUP BY r.id
            ORDER BY useful DESC
            LIMIT ?
            """;

    public static final String SAVE = """
            INSERT INTO reviews (content, is_positive, user_id, film_id) VALUES (?, ?, ?, ?)
            """;

    public static final String UPDATE = """
            UPDATE reviews SET content = ?, is_positive = ?, user_id = ?, film_id = ? WHERE id = ?
            """;

    public static final String DELETE = """
            DELETE FROM reviews WHERE id = ?
            """;

    public static final String CREATE_REACTION = """
            MERGE INTO review_likes (review_id, user_id, is_like) KEY(review_id, user_id) VALUES (?, ?, ?)
            """;

    public static final String DELETE_REACTION = """
            DELETE FROM review_likes WHERE review_id = ? AND user_id = ?
            """;

    public static final String DELETE_REACTION_BY_TYPE = """
            DELETE FROM review_likes WHERE review_id = ? AND user_id = ? AND is_like = ?
            """;
}
