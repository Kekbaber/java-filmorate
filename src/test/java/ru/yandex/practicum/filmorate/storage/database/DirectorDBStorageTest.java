package ru.yandex.practicum.filmorate.storage.database;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import ru.yandex.practicum.filmorate.exception.model.InternalServerException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.db.DirectorDBStorage;
import ru.yandex.practicum.filmorate.storage.db.mappers.DirectorRowMapper;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({DirectorDBStorage.class, DirectorRowMapper.class})
public class DirectorDBStorageTest {

    @Autowired
    private DirectorStorage storage;

    @Autowired
    private JdbcTemplate jdbc;

    @BeforeEach
    void cleanUp() {
        jdbc.execute("DELETE FROM director_films");
        jdbc.execute("DELETE FROM films");
        jdbc.execute("ALTER TABLE films ALTER COLUMN id RESTART WITH 1");
        jdbc.execute("DELETE FROM directors");
        jdbc.execute("ALTER TABLE directors ALTER COLUMN id RESTART WITH 1");
    }

    private Director createDirector(String name) {
        Director director = new Director();
        director.setName(name);
        return director;
    }

    private long addFilmToDb(String name) {
        String sql = "INSERT INTO films (name, description, release_date, duration, rating_id) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            var ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, name);
            ps.setString(2, "desc");
            ps.setDate(3, java.sql.Date.valueOf(LocalDate.now()));
            ps.setInt(4, 120);
            ps.setInt(5, 1);
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Test
    void findAll_WhenEmpty_ShouldReturnEmpty() {
        Collection<Director> directors = storage.findAll();
        assertThat(directors).isEmpty();
    }

    @Test
    void findAll_ShouldReturnAllSavedDirectors() {
        Director director1 = createDirector("Director1");
        Director director2 = createDirector("Director2");

        storage.create(director1);
        storage.create(director2);

        Collection<Director> directors = storage.findAll();
        assertThat(directors).hasSize(2);
        assertThat(directors).extracting(Director::getName).containsExactlyInAnyOrder("Director1", "Director2");
    }

    @Test
    void findById_WhenNotFound_ShouldReturnEmpty() {
        Optional<Director> director = storage.findById(999L);
        assertThat(director).isEmpty();
    }

    @Test
    void findById_WhenExists_ShouldReturnDirector() {
        Director saved = storage.create(createDirector("Director1"));
        long id = saved.getId();

        Optional<Director> found = storage.findById(id);
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Director1");
    }

    @Test
    void create_ShouldGenerateIdAndSaveDirector() {
        Director newDirector = createDirector("Director1");
        Director created = storage.create(newDirector);

        assertThat(created.getId()).isPositive();
        Optional<Director> fetched = storage.findById(created.getId());
        assertThat(fetched).isPresent();
        assertThat(fetched.get().getName()).isEqualTo("Director1");
    }

    @Test
    void update_ShouldChangeFields() {
        Director original = storage.create(createDirector("Old director"));
        long id = original.getId();

        original.setName("New Name");

        storage.update(original);

        Optional<Director> updated = storage.findById(id);
        assertThat(updated).isPresent();
        assertThat(updated.get())
                .hasFieldOrPropertyWithValue("name", "New Name");
    }

    @Test
    void update_WhenDirectorNotExists_ShouldThrowInternalServerException() {
        Director nonExistent = createDirector("Director1");
        nonExistent.setId(999L); // несуществующий ID
        assertThatThrownBy(() -> storage.update(nonExistent))
                .isInstanceOf(InternalServerException.class)
                .hasMessage("Не удалось обновить данные");

        assertThat(storage.findAll()).isEmpty();
    }

    @Test
    void delete_ShouldRemoveDirector() {
        Director toDelete = storage.create(createDirector("Director1"));
        long id = toDelete.getId();

        storage.delete(id);

        Optional<Director> deleted = storage.findById(id);
        assertThat(deleted).isEmpty();
    }

    @Test
    void delete_WhenDirectorNotExists_ShouldDoNothing() {
        storage.delete(999L); // не должно быть исключений
        // Просто проверяем, что всё нормально
        assertThat(storage.findAll()).isEmpty();
    }

    @Test
    void addDirectorsToFilm_ShouldAddOneDirectorToFilm() {
        long createdFilmId = addFilmToDb("Film1");
        storage.create(createDirector("Director1"));

        storage.addDirectorsToFilm(1L, List.of(1L));

        Integer count = jdbc.queryForObject("""
            SELECT COUNT(*)
            FROM director_films
            WHERE film_id = 1
            """, Integer.class);

        assertThat(count).isEqualTo(1);
    }

    @Test
    void addDirectorsToFilm_ShouldAddSeveralDirectorsToFilm() {
        long createdFilmId = addFilmToDb("Film1");
        storage.create(createDirector("Director1"));
        storage.create(createDirector("Director1"));

        storage.addDirectorsToFilm(1L, List.of(1L, 2L));

        Integer count = jdbc.queryForObject("""
            SELECT COUNT(*)
            FROM director_films
            WHERE film_id = 1
            """, Integer.class);

        assertThat(count).isEqualTo(2);
    }

    @Test
    void findByFilmIds_ShouldReturnDirectorsForSingleFilm() {
        long createdFilmId = addFilmToDb("Film1");
        storage.create(createDirector("Director1"));
        storage.create(createDirector("Director2"));

        storage.addDirectorsToFilm(createdFilmId, List.of(1L, 2L));

        Map<Long, List<Director>> result =
                storage.findByFilmIds(Set.of(createdFilmId));

        assertThat(result).hasSize(1);

        List<Director> directors = result.get(createdFilmId);

        assertThat(directors).isNotNull();
        assertThat(directors).hasSize(2);

        assertThat(
                directors.stream()
                        .map(Director::getName)
                        .toList()
        ).containsAll(List.of("Director1", "Director2"));
    }

    @Test
    void findByFilmIds_ShouldReturnDirectorsForSeveralFilms() {
        long filmId1 = addFilmToDb("Film1");
        long filmId2 = addFilmToDb("Film2");
        storage.create(createDirector("Director1"));
        storage.create(createDirector("Director2"));

        storage.addDirectorsToFilm(filmId1, List.of(1L, 2L));
        storage.addDirectorsToFilm(filmId2, List.of(2L));

        Map<Long, List<Director>> result =
                storage.findByFilmIds(Set.of(filmId1, filmId2));

        assertThat(result).hasSize(2);

        // проверка фильма 1

        List<Director> film1Directors = result.get(filmId1);

        assertThat(film1Directors).isNotNull();
        assertThat(film1Directors).hasSize(2);

        assertThat(
                film1Directors.stream()
                        .map(Director::getName)
                        .toList()
        ).containsAll(List.of("Director1", "Director2"));

        // проверка фильма 2

        List<Director> film2Directors = result.get(filmId2);

        assertThat(film2Directors).isNotNull();
        assertThat(film2Directors).hasSize(1);

        assertThat(
                film2Directors.stream()
                        .map(Director::getName)
                        .toList()
        ).containsAll(List.of("Director2"));
    }

    @Test
    void findByFilmIds_ShouldReturnEmptyMapWhenFilmIdsEmpty() {
        Map<Long, List<Director>> result =
                storage.findByFilmIds(Set.of());

        assertThat(result).isEmpty();
    }

    @Test
    void findByFilmIds_ShouldReturnEmptyMapWhenNoFilmsFound() {
        Map<Long, List<Director>> result =
                storage.findByFilmIds(Set.of(999L));

        assertThat(result).isEmpty();
    }

    @Test
    void findExistingIds_ShouldReturnAllExistingIds() {
        storage.create(createDirector("Director1"));
        storage.create(createDirector("Director2"));
        storage.create(createDirector("Director3"));

        Set<Long> existingIds = storage.findExistingIds(Set.of(1L, 2L, 3L));

        assertThat(existingIds)
                .containsExactlyInAnyOrder(1L, 2L, 3L);
    }

    @Test
    void shouldReturnOnlyExistingIds() {
        storage.create(createDirector("Director1"));
        storage.create(createDirector("Director2"));

        Set<Long> existingIds = storage.findExistingIds(Set.of(1L, 2L, 999L));

        assertThat(existingIds)
                .containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    void shouldReturnEmptySetWhenNoIdsExist() {
        storage.create(createDirector("Director1"));
        storage.create(createDirector("Director2"));
        storage.create(createDirector("Director3"));

        Set<Long> existingIds = storage.findExistingIds(Set.of(999L, 1000L));

        assertThat(existingIds).isEmpty();
    }

    @Test
    void shouldReturnEmptySetForEmptyInput() {
        storage.create(createDirector("Director1"));

        assertThat(storage.findExistingIds(Set.of())).isEmpty();
    }

}
