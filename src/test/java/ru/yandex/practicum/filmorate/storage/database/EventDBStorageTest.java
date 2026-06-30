package ru.yandex.practicum.filmorate.storage.database;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.db.EventDBStorage;
import ru.yandex.practicum.filmorate.storage.db.mappers.EventRowMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({EventDBStorage.class, EventRowMapper.class})
class EventDBStorageTest {

    @Autowired
    private final EventStorage eventStorage;

    @Autowired
    private final JdbcTemplate jdbc;

    private long createTestUser(String email, String login) {
        jdbc.update("INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)",
                email, login, login, java.sql.Date.valueOf("2000-01-01"));
        return jdbc.queryForObject("SELECT id FROM users WHERE email = ?", Long.class, email);
    }

    @BeforeEach
    void cleanTables() {
        jdbc.execute("DELETE FROM events");
        jdbc.execute("DELETE FROM review_likes");
        jdbc.execute("DELETE FROM reviews");
        jdbc.execute("DELETE FROM likes");
        jdbc.execute("DELETE FROM friendships");
        jdbc.execute("DELETE FROM film_genre");
        jdbc.execute("DELETE FROM films");
        jdbc.execute("DELETE FROM users");
        jdbc.execute("ALTER TABLE events ALTER COLUMN event_id RESTART WITH 1");
        jdbc.execute("ALTER TABLE users ALTER COLUMN id RESTART WITH 1");
    }

    @Test
    void findByUserId_WhenEmpty_ShouldReturnEmpty() {
        long userId = createTestUser("user@mail.ru", "user");
        List<Event> events = eventStorage.findByUserId(userId);
        assertThat(events).isEmpty();
    }

    @Test
    void save_And_FindByUserId_ShouldReturnSavedEvent() {
        long userId = createTestUser("user@mail.ru", "user");

        Event event = Event.of(userId, 1L, EventType.FRIEND, EventOperation.ADD);
        eventStorage.save(event);

        List<Event> events = eventStorage.findByUserId(userId);
        assertThat(events).hasSize(1);
        Event saved = events.getFirst();
        assertThat(saved.getEventId()).isPositive();
        assertThat(saved.getUserId()).isEqualTo(userId);
        assertThat(saved.getEntityId()).isEqualTo(1L);
        assertThat(saved.getEventType()).isEqualTo(EventType.FRIEND);
        assertThat(saved.getOperation()).isEqualTo(EventOperation.ADD);
        assertThat(saved.getTimestamp()).isPositive();
    }

    @Test
    void findByUserId_ShouldReturnEventsOrderedByEventId() {
        long userId = createTestUser("user@mail.ru", "user");

        Event first = Event.of(userId, 1L, EventType.LIKE, EventOperation.ADD);
        eventStorage.save(first);
        Event second = Event.of(userId, 2L, EventType.FRIEND, EventOperation.ADD);
        eventStorage.save(second);

        List<Event> events = eventStorage.findByUserId(userId);
        assertThat(events).hasSize(2);
        assertThat(events.get(0).getEventId()).isLessThan(events.get(1).getEventId());
    }

    @Test
    void findByUserId_ShouldNotReturnEventsOfOtherUsers() {
        long userA = createTestUser("a@mail.ru", "userA");
        long userB = createTestUser("b@mail.ru", "userB");

        eventStorage.save(Event.of(userA, 1L, EventType.LIKE, EventOperation.ADD));
        eventStorage.save(Event.of(userB, 2L, EventType.FRIEND, EventOperation.ADD));

        List<Event> eventsForA = eventStorage.findByUserId(userA);
        assertThat(eventsForA).hasSize(1);
        assertThat(eventsForA.getFirst().getUserId()).isEqualTo(userA);

        List<Event> eventsForB = eventStorage.findByUserId(userB);
        assertThat(eventsForB).hasSize(1);
        assertThat(eventsForB.getFirst().getUserId()).isEqualTo(userB);
    }

    @Test
    void save_WhenUserNotExists_ShouldThrowException() {
        Event event = Event.of(999L, 1L, EventType.LIKE, EventOperation.ADD);
        assertThatThrownBy(() -> eventStorage.save(event))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}