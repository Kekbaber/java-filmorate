package ru.yandex.practicum.filmorate.storage.database;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import ru.yandex.practicum.filmorate.storage.LikeStorage;
import ru.yandex.practicum.filmorate.storage.db.LikeDBStorage;
import ru.yandex.practicum.filmorate.storage.db.mappers.LikeRowMapper;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({LikeDBStorage.class, LikeRowMapper.class})
class LikeDBStorageTest {

    @Autowired
    private final LikeStorage likeStorage;

    @Autowired
    private final JdbcTemplate jdbc;

    // Вспомогательные методы для создания тестовых данных
    private long createTestFilm(String name) {
        String sql = "INSERT INTO films (name, description, release_date, duration, rating_id) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.setString(2, "Test description");
            ps.setDate(3, java.sql.Date.valueOf(LocalDate.now()));
            ps.setInt(4, 120);
            ps.setInt(5, 1);
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    private long createTestUser(String email, String login) {
        String sql = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, email);
            ps.setString(2, login);
            ps.setString(3, login); // имя = логин для простоты
            ps.setDate(4, java.sql.Date.valueOf(LocalDate.of(2000, 1, 1)));
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @BeforeEach
    void cleanTables() {
        jdbc.execute("DELETE FROM likes");
        jdbc.execute("DELETE FROM films");
        jdbc.execute("DELETE FROM users");
        jdbc.execute("ALTER TABLE films ALTER COLUMN id RESTART WITH 1");
        jdbc.execute("ALTER TABLE users ALTER COLUMN id RESTART WITH 1");
    }

    @Test
    void findUserIdsByFilmId_WhenNoLikes_ShouldReturnEmptySet() {
        long filmId = createTestFilm("Film without likes");
        Set<Long> userIds = likeStorage.findUserIdsByFilmId(filmId);
        assertThat(userIds).isEmpty();
    }

    @Test
    void findUserIdsByFilmId_WhenSomeLikes_ShouldReturnUserIdsSet() {
        long filmId = createTestFilm("Liked film");
        long user1 = createTestUser("user1@mail.ru", "user1");
        long user2 = createTestUser("user2@mail.ru", "user2");

        likeStorage.addLike(filmId, user1);
        likeStorage.addLike(filmId, user2);

        Set<Long> userIds = likeStorage.findUserIdsByFilmId(filmId);
        assertThat(userIds).hasSize(2).containsExactlyInAnyOrder(user1, user2);
    }

    @Test
    void addLike_ShouldInsertLike() {
        long filmId = createTestFilm("Film for add");
        long userId = createTestUser("add@mail.ru", "addUser");

        likeStorage.addLike(filmId, userId);

        Set<Long> likes = likeStorage.findUserIdsByFilmId(filmId);
        assertThat(likes).containsExactly(userId);
    }

    @Test
    void addLike_WhenDuplicate_ShouldThrowDuplicateKeyException() {
        long filmId = createTestFilm("Film duplicate");
        long userId = createTestUser("dup@mail.ru", "dupUser");

        likeStorage.addLike(filmId, userId); // первый раз успешно

        // второй раз тот же лайк -> нарушение PRIMARY KEY
        assertThatThrownBy(() -> likeStorage.addLike(filmId, userId))
                .isInstanceOf(DuplicateKeyException.class);
    }

    @Test
    void addLike_WhenFilmNotExists_ShouldThrowException() {
        long nonExistentFilmId = 999L;
        long userId = createTestUser("filmless@mail.ru", "filmless");

        assertThatThrownBy(() -> likeStorage.addLike(nonExistentFilmId, userId))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void addLike_WhenUserNotExists_ShouldThrowException() {
        long filmId = createTestFilm("Film with ghost user");
        long nonExistentUserId = 999L;

        assertThatThrownBy(() -> likeStorage.addLike(filmId, nonExistentUserId))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void delete_ShouldDeleteLike() {
        long filmId = createTestFilm("Film for remove");
        long userId = createTestUser("remove@mail.ru", "removeUser");

        likeStorage.addLike(filmId, userId);
        assertThat(likeStorage.findUserIdsByFilmId(filmId)).hasSize(1);

        likeStorage.delete(filmId, userId);
        assertThat(likeStorage.findUserIdsByFilmId(filmId)).isEmpty();
    }

    @Test
    void delete_WhenLikeNotExists_ShouldDoNothing() {
        long filmId = createTestFilm("Film no like");
        long userId = createTestUser("noremove@mail.ru", "noremove");

        // удаление несуществующей записи не должно падать
        likeStorage.delete(filmId, userId);
        // проверяем, что ничего не сломалось
        assertThat(likeStorage.findUserIdsByFilmId(filmId)).isEmpty();
    }
}
