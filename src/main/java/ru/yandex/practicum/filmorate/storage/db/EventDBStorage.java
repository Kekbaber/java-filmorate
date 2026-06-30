package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.db.queries.EventQueries;

import java.util.List;

@Repository
@Profile("database")
@Slf4j
public class EventDBStorage extends BaseStorage<Event> implements EventStorage {

    public EventDBStorage(JdbcTemplate jdbc, RowMapper<Event> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Event> findByUserId(long userId) {
        return findMany(EventQueries.FIND_BY_USER_ID, userId);
    }

    @Override
    public void save(Event event) {
        long id = insert(EventQueries.SAVE,
                event.getUserId(),
                event.getEntityId(),
                event.getEventType().name(),
                event.getOperation().name(),
                event.getTimestamp()
        );
        log.debug("DB: created event id={}", id);
    }
}
