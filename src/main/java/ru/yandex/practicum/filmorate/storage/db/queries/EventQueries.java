package ru.yandex.practicum.filmorate.storage.db.queries;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventQueries {

    public static final String FIND_BY_USER_ID = """
            SELECT *
            FROM events WHERE user_id = ?
            ORDER BY timestamp DESC
            """;

    public static final String SAVE = """
            INSERT INTO events(user_id, entity_id, event_type, operation,  timestamp)
            VALUES(?, ?, ?, ?, ?)
            """;
}
