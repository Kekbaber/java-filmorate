package ru.yandex.practicum.filmorate.storage.database;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import ru.yandex.practicum.filmorate.exception.model.InternalServerException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmSortType;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.db.FilmDBStorage;
import ru.yandex.practicum.filmorate.storage.db.mappers.FilmRowMapper;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmDBStorage.class, FilmRowMapper.class})
class FilmDBStorageTest {

    @Autowired
    private final FilmStorage filmStorage;

    @Autowired
    private final JdbcTemplate jdbc;

    // Вспомогательный метод для создания объекта Film с заданными параметрами
    private Film createFilm(String name, String description, LocalDate releaseDate, long duration, long mpaId) {
        Film film = new Film();
        film.setName(name);
        film.setDescription(description);
        film.setReleaseDate(releaseDate);
        film.setDuration(duration);
        Mpa mpa = new Mpa();
        mpa.setId(mpaId);
        film.setMpa(mpa);
        return film;
    }

    private long createTestUser(String email, String login) {
        String sql = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, email);
            ps.setString(2, login);
            ps.setString(3, login);
            ps.setDate(4, java.sql.Date.valueOf(LocalDate.of(2000, 1, 1)));
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    private long createTestDirector(String name) {
        String sql = "INSERT INTO directors (name) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    private void addLike(long filmId, long userId) {
        jdbc.update("INSERT INTO likes(film_id, user_id) VALUES(?, ?)", filmId, userId);
    }

    private void addGenreToFilm(long filmId, long genreId) {
        jdbc.update("INSERT INTO film_genre(film_id, genre_id) VALUES(?, ?)", filmId, genreId);
    }

    private void addDirector(long filmId, long directorId) {
        jdbc.update("INSERT INTO director_films(film_id, director_id) VALUES(?, ?)", filmId, directorId);
    }

    // Очистка таблицы фильмов перед каждым тестом и сброс автоинкремента
    @BeforeEach
    void cleanTable() {
        jdbc.execute("DELETE FROM film_genre");
        jdbc.execute("DELETE FROM likes");
        jdbc.execute("DELETE FROM friendships");
        jdbc.execute("DELETE FROM films");
        jdbc.execute("DELETE FROM users");
        jdbc.execute("ALTER TABLE films ALTER COLUMN id RESTART WITH 1");
        jdbc.execute("ALTER TABLE users ALTER COLUMN id RESTART WITH 1");
    }

    @Test
    void findAll_WhenEmpty_ShouldReturnEmpty() {
        Collection<Film> films = filmStorage.findAll();
        assertThat(films).isEmpty();
    }

    @Test
    void findAll_ShouldReturnAllSavedFilms() {
        Film film1 = createFilm("Film1", "Desc1", LocalDate.of(2020, 1, 1), 120, 1);
        Film film2 = createFilm("Film2", "Desc2", LocalDate.of(2021, 2, 2), 90, 2);

        filmStorage.create(film1);
        filmStorage.create(film2);

        Collection<Film> films = filmStorage.findAll();
        assertThat(films).hasSize(2);
        assertThat(films).extracting(Film::getName).containsExactlyInAnyOrder("Film1", "Film2");
    }

    @Test
    void findById_WhenNotFound_ShouldReturnEmpty() {
        Optional<Film> film = filmStorage.findById(999L);
        assertThat(film).isEmpty();
    }

    @Test
    void findById_WhenExists_ShouldReturnFilm() {
        Film saved = filmStorage.create(createFilm("Findable", "Findable desc", LocalDate.of(1999, 5, 5), 110, 1));
        long id = saved.getId();

        Optional<Film> found = filmStorage.findById(id);
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Findable");
        assertThat(found.get().getMpa().getId()).isEqualTo(1L);
    }

    @Test
    void create_ShouldGenerateIdAndSaveFilm() {
        Film newFilm = createFilm("New Film", "New description", LocalDate.of(2022, 12, 12), 150, 3);
        Film created = filmStorage.create(newFilm);

        assertThat(created.getId()).isPositive();
        Optional<Film> fetched = filmStorage.findById(created.getId());
        assertThat(fetched).isPresent();
        assertThat(fetched.get().getName()).isEqualTo("New Film");
        assertThat(fetched.get().getDescription()).isEqualTo("New description");
    }

    @Test
    void create_WhenMpaIdNotExists_ShouldThrowDataIntegrityViolationException() {
        Film invalidFilm = createFilm("Bad Film", "Bad desc", LocalDate.of(2020, 1, 1), 100, 999L);
        assertThatThrownBy(() -> filmStorage.create(invalidFilm))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void update_ShouldChangeFields() {
        Film original = filmStorage.create(createFilm("Old Name", "Old desc", LocalDate.of(2000, 1, 1), 100, 1));
        long id = original.getId();

        original.setName("New Name");
        original.setDescription("New desc");
        original.setReleaseDate(LocalDate.of(2001, 2, 2));
        original.setDuration(200L);
        original.getMpa().setId(2);

        filmStorage.update(original);

        Optional<Film> updated = filmStorage.findById(id);
        assertThat(updated).isPresent();
        assertThat(updated.get())
                .hasFieldOrPropertyWithValue("name", "New Name")
                .hasFieldOrPropertyWithValue("description", "New desc")
                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(2001, 2, 2))
                .hasFieldOrPropertyWithValue("duration", 200L)
                .hasFieldOrPropertyWithValue("mpa.id", 2L);
    }

    @Test
    void update_WhenFilmNotExists_ShouldThrowInternalServerException() {
        Film nonExistent = createFilm("Ghost", "Ghost desc", LocalDate.of(2000, 1, 1), 100, 1);
        nonExistent.setId(999L); // несуществующий ID
        assertThatThrownBy(() -> filmStorage.update(nonExistent))
                .isInstanceOf(InternalServerException.class)
                .hasMessage("Не удалось обновить данные");

        // Дополнительно проверяем, что база данных не изменилась (осталась пустой)
        assertThat(filmStorage.findAll()).isEmpty();
    }

    @Test
    void delete_ShouldRemoveFilm() {
        Film toDelete = filmStorage.create(createFilm("ToDelete", "Delete me", LocalDate.of(2010, 10, 10), 80, 1));
        long id = toDelete.getId();

        filmStorage.delete(id);

        Optional<Film> deleted = filmStorage.findById(id);
        assertThat(deleted).isEmpty();
    }

    @Test
    void delete_WhenFilmNotExists_ShouldDoNothing() {
        filmStorage.delete(999L); // не должно быть исключений
        // Просто проверяем, что всё нормально
        assertThat(filmStorage.findAll()).isEmpty();
    }

    @Test
    void create_WhenReleaseDateTooEarly_ShouldThrowException() {
        Film invalidFilm = createFilm("Ancient", "Too old", LocalDate.of(1800, 1, 1), 120, 1);
        // В схеме есть CONSTRAINT chk_release_date CHECK (release_date >= '1895-12-28')
        assertThatThrownBy(() -> filmStorage.create(invalidFilm))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void create_WhenDurationZeroOrNegative_ShouldThrowException() {
        Film invalidFilm = createFilm("Zero duration", "No time", LocalDate.of(2000, 1, 1), 0, 1);
        // CONSTRAINT chk_duration CHECK (duration > 0)
        assertThatThrownBy(() -> filmStorage.create(invalidFilm))
                .isInstanceOf(DataIntegrityViolationException.class);

        invalidFilm.setDuration(-10L);
        assertThatThrownBy(() -> filmStorage.create(invalidFilm))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void findPopularFilms_WhenNoLikes_ShouldReturnAllFilms() {
        filmStorage.create(createFilm("Film A", "Desc A", LocalDate.of(2000, 1, 1), 100, 1));
        filmStorage.create(createFilm("Film B", "Desc B", LocalDate.of(2001, 2, 2), 120, 2));

        List<Film> popular = filmStorage.findPopularFilms(10, null, null);
        assertThat(popular).hasSize(2);
    }

    @Test
    void findPopularFilms_ShouldReturnFilmsOrderedByLikeCount() {
        long filmA = filmStorage.create(createFilm("A", "Desc A", LocalDate.of(2000, 1, 1), 100, 1)).getId();
        long filmB = filmStorage.create(createFilm("B", "Desc B", LocalDate.of(2001, 2, 2), 120, 2)).getId();
        long filmC = filmStorage.create(createFilm("C", "Desc C", LocalDate.of(2002, 3, 3), 90, 3)).getId();

        long u1 = createTestUser("u1@mail.ru", "user1");
        long u2 = createTestUser("u2@mail.ru", "user2");
        long u3 = createTestUser("u3@mail.ru", "user3");
        long u4 = createTestUser("u4@mail.ru", "user4");

        addLike(filmA, u1);
        addLike(filmA, u2);
        addLike(filmB, u1);
        addLike(filmB, u2);
        addLike(filmB, u3);
        addLike(filmC, u4);

        List<Film> top2 = filmStorage.findPopularFilms(2, null, null);
        assertThat(top2).hasSize(2);
        assertThat(top2.get(0).getId()).isEqualTo(filmB);
        assertThat(top2.get(1).getId()).isEqualTo(filmA);

        List<Film> top5 = filmStorage.findPopularFilms(5, null, null);
        assertThat(top5).hasSize(3);
        assertThat(top5).extracting(Film::getId).containsExactly(filmB, filmA, filmC);
    }

    @Test
    void findPopularFilms_WhenSameLikeCount_ShouldOrderByFilmIdAsc() {
        long filmX = filmStorage.create(createFilm("X", "Desc X", LocalDate.of(2000, 1, 1), 100, 1)).getId();
        long filmY = filmStorage.create(createFilm("Y", "Desc Y", LocalDate.of(2001, 2, 2), 120, 2)).getId();

        long u1 = createTestUser("u1@mail.ru", "user1");
        long u2 = createTestUser("u2@mail.ru", "user2");

        addLike(filmX, u1);
        addLike(filmY, u2);

        List<Film> result = filmStorage.findPopularFilms(10, null, null);
        assertThat(result).extracting(Film::getId).containsExactly(filmX, filmY);
    }

    @Test
    void findPopularFilms_ShouldRespectLimit() {
        long film1 = filmStorage.create(createFilm("F1", "D1", LocalDate.of(2000, 1, 1), 100, 1)).getId();
        long film2 = filmStorage.create(createFilm("F2", "D2", LocalDate.of(2001, 2, 2), 120, 2)).getId();
        long film3 = filmStorage.create(createFilm("F3", "D3", LocalDate.of(2002, 3, 3), 90, 3)).getId();

        long u1 = createTestUser("u1@mail.ru", "u1");
        long u2 = createTestUser("u2@mail.ru", "u2");
        long u3 = createTestUser("u3@mail.ru", "u3");

        addLike(film1, u1);
        addLike(film2, u1);
        addLike(film2, u2);
        addLike(film3, u1);
        addLike(film3, u2);
        addLike(film3, u3);

        List<Film> top1 = filmStorage.findPopularFilms(1, null, null);
        assertThat(top1).hasSize(1);
        assertThat(top1.getFirst().getId()).isEqualTo(film3);

        List<Film> top2 = filmStorage.findPopularFilms(2, null, null);
        assertThat(top2).hasSize(2);
        assertThat(top2).extracting(Film::getId).containsExactly(film3, film2);
    }

    @Test
    @DisplayName("Поиск популярных фильмов с фильтрацией по жанру")
    void findPopularFilms_WhenFilterByGenre_ShouldReturnOnlyFilmsWithThatGenre() {
        Film film1 = filmStorage.create(createFilm("Film1", "Desc1", LocalDate.of(2000, 1, 1), 100, 1));
        Film film2 = filmStorage.create(createFilm("Film2", "Desc2", LocalDate.of(2001, 2, 2), 120, 2));
        Film film3 = filmStorage.create(createFilm("Film3", "Desc3", LocalDate.of(2002, 3, 3), 90, 3));

        addGenreToFilm(film1.getId(), 1); // Комедия
        addGenreToFilm(film2.getId(), 2); // Драма
        addGenreToFilm(film3.getId(), 1L); // Комедия

        long u1 = createTestUser("u1@mail.ru", "user1");
        long u2 = createTestUser("u2@mail.ru", "user2");
        addLike(film1.getId(), u1);
        addLike(film2.getId(), u1);
        addLike(film2.getId(), u2);
        addLike(film3.getId(), u1);

        // Фильтр по жанру 1 (Комедия) – должны вернуться film1 и film3, отсортированные по лайкам
        List<Film> result = filmStorage.findPopularFilms(10, 1L, null);
        assertThat(result).hasSize(2);
        // Сначала film1 (1 лайк), затем film3 (1 лайк) – при равных лайках порядок по id (film1.id < film3.id)
        assertThat(result).extracting(Film::getId).containsExactly(film1.getId(), film3.getId());

        // Фильтр по жанру 2 (Драма) – должен вернуться только film2
        result = filmStorage.findPopularFilms(10, 2L, null);
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(film2.getId());
    }

    @Test
    @DisplayName("Поиск популярных фильмов с фильтрацией по году")
    void findPopularFilms_WhenFilterByYear_ShouldReturnOnlyFilmsWithThatReleaseYear() {
        Film film1 = filmStorage.create(createFilm("Film1", "Desc1", LocalDate.of(2000, 1, 1), 100, 1));
        Film film2 = filmStorage.create(createFilm("Film2", "Desc2", LocalDate.of(2001, 2, 2), 120, 2));
        Film film3 = filmStorage.create(createFilm("Film3", "Desc3", LocalDate.of(2000, 3, 3), 90, 3));

        long u1 = createTestUser("u1@mail.ru", "user1");
        long u2 = createTestUser("u2@mail.ru", "user2");
        addLike(film1.getId(), u1);
        addLike(film2.getId(), u1);
        addLike(film2.getId(), u2);
        addLike(film3.getId(), u1);

        // Фильтр по году 2000 – должны вернуться film1 и film3
        List<Film> result = filmStorage.findPopularFilms(10, null, 2000);
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Film::getId).containsExactly(film1.getId(), film3.getId());

        // Фильтр по году 2001 – должен вернуться film2
        result = filmStorage.findPopularFilms(10, null, 2001);
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(film2.getId());
    }

    @Test
    @DisplayName("Поиск популярных фильмов с фильтрацией по жанру и году")
    void findPopularFilms_WhenFilterByGenreAndYear_ShouldReturnOnlyMatchingFilms() {
        // Создаём 4 фильма
        Film film1 = filmStorage.create(createFilm("Film1", "Desc1", LocalDate.of(2000, 1, 1), 100, 1));
        Film film2 = filmStorage.create(createFilm("Film2", "Desc2", LocalDate.of(2000, 2, 2), 120, 2));
        Film film3 = filmStorage.create(createFilm("Film3", "Desc3", LocalDate.of(2000, 3, 3), 90, 3)); // год 2000, жанр 2
        Film film4 = filmStorage.create(createFilm("Film4", "Desc4", LocalDate.of(2001, 4, 4), 110, 1)); // год 2001, жанр 1

        // Добавляем жанры
        addGenreToFilm(film1.getId(), 1L);
        addGenreToFilm(film2.getId(), 1L); // film2 тоже комедия
        addGenreToFilm(film3.getId(), 2L); // драма
        addGenreToFilm(film4.getId(), 1L); // комедия, но год 2001

        // Создаём пользователей для лайков (4 пользователя)
        long u1 = createTestUser("u1@mail.ru", "user1");
        long u2 = createTestUser("u2@mail.ru", "user2");
        long u3 = createTestUser("u3@mail.ru", "user3");
        long u4 = createTestUser("u4@mail.ru", "user4");

        // Добавляем лайки:
        // film1: 1 лайк (u1)
        addLike(film1.getId(), u1);
        // film2: 2 лайка (u1, u2)
        addLike(film2.getId(), u1);
        addLike(film2.getId(), u2);
        // film3: 3 лайка (u1, u2, u3) – но жанр 2, не попадает в фильтр
        addLike(film3.getId(), u1);
        addLike(film3.getId(), u2);
        addLike(film3.getId(), u3);
        // film4: 4 лайка (u1, u2, u3, u4) – но год 2001, не попадает в фильтр
        addLike(film4.getId(), u1);
        addLike(film4.getId(), u2);
        addLike(film4.getId(), u3);
        addLike(film4.getId(), u4);

        // Фильтр: жанр 1 и год 2000 – должны вернуться film1 и film2, отсортированные по лайкам (film2 – 2 лайка, film1 – 1)
        List<Film> result = filmStorage.findPopularFilms(10, 1L, 2000);
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Film::getId).containsExactly(film2.getId(), film1.getId()); // film2 имеет 2 лайка, film1 – 1

        // Проверяем, что film3 и film4 не попали
        assertThat(result).extracting(Film::getId).doesNotContain(film3.getId(), film4.getId());

        // Жанр 1 и год 2001 – должен вернуться только film4
        result = filmStorage.findPopularFilms(10, 1L, 2001);
        assertThat(result).hasSize(1);
        assertThat(result).extracting(Film::getId).containsExactly(film4.getId());
    }

    @Test
    @DisplayName("Поиск популярных фильмов с несуществующим жанром возвращает пустой список")
    void findPopularFilms_WhenGenreNotFound_ShouldReturnEmpty() {
        Film film = filmStorage.create(createFilm("Film", "Desc", LocalDate.of(2000, 1, 1), 100, 1));
        addGenreToFilm(film.getId(),1L);
        long u1 = createTestUser("u1@mail.ru", "user1");
        addLike(film.getId(), u1);

        List<Film> result = filmStorage.findPopularFilms(10, 999L, null);
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Поиск популярных фильмов с несуществующим годом возвращает пустой список")
    void findPopularFilms_WhenYearNotFound_ShouldReturnEmpty() {
        Film film = filmStorage.create(createFilm("Film", "Desc", LocalDate.of(2000, 1, 1), 100, 1));
        long u1 = createTestUser("u1@mail.ru", "user1");
        addLike(film.getId(), u1);

        List<Film> result = filmStorage.findPopularFilms(10, null, 1999);
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Поиск общих фильмов: нет общих фильмов")
    void findCommonFilms_WhenNoCommon_ShouldReturnEmpty() {
        Film film1 = filmStorage.create(createFilm("Film1", "Desc1", LocalDate.of(2000, 1, 1), 100, 1));
        Film film2 = filmStorage.create(createFilm("Film2", "Desc2", LocalDate.of(2001, 2, 2), 120, 2));

        long u1 = createTestUser("u1@mail.ru", "user1");
        long u2 = createTestUser("u2@mail.ru", "user2");

        addLike(film1.getId(), u1);
        addLike(film2.getId(), u2);

        List<Film> common = filmStorage.findCommonFilms(u1, u2);
        assertThat(common).isEmpty();
    }

    @Test
    @DisplayName("Поиск общих фильмов: один общий фильм")
    void findCommonFilms_WhenOneCommon_ShouldReturnThatFilm() {
        Film film1 = filmStorage.create(createFilm("Film1", "Desc1", LocalDate.of(2000, 1, 1), 100, 1));
        Film film2 = filmStorage.create(createFilm("Film2", "Desc2", LocalDate.of(2001, 2, 2), 120, 2));
        Film film3 = filmStorage.create(createFilm("Film3", "Desc3", LocalDate.of(2002, 3, 3), 90, 3));

        long u1 = createTestUser("u1@mail.ru", "user1");
        long u2 = createTestUser("u2@mail.ru", "user2");

        addLike(film1.getId(), u1);
        addLike(film1.getId(), u2); // общий
        addLike(film2.getId(), u1);
        addLike(film3.getId(), u2);

        List<Film> common = filmStorage.findCommonFilms(u1, u2);
        assertThat(common).hasSize(1);
        assertThat(common.get(0).getId()).isEqualTo(film1.getId());
    }

    @Test
    @DisplayName("Поиск общих фильмов: несколько общих, сортировка по популярности")
    void findCommonFilms_WhenMultipleCommon_ShouldReturnSortedByLikes() {
        Film filmA = filmStorage.create(createFilm("FilmA", "DescA", LocalDate.of(2000, 1, 1), 100, 1));
        Film filmB = filmStorage.create(createFilm("FilmB", "DescB", LocalDate.of(2001, 2, 2), 120, 2));
        Film filmC = filmStorage.create(createFilm("FilmC", "DescC", LocalDate.of(2002, 3, 3), 90, 3));
        Film filmD = filmStorage.create(createFilm("FilmD", "DescD", LocalDate.of(2003, 4, 4), 110, 1));

        long u1 = createTestUser("u1@mail.ru", "user1");
        long u2 = createTestUser("u2@mail.ru", "user2");
        long u3 = createTestUser("u3@mail.ru", "user3");

        // Общие для u1 и u2: filmA, filmB, filmC
        addLike(filmA.getId(), u1);
        addLike(filmA.getId(), u2); // общий
        addLike(filmA.getId(), u3); // дополнительный лайк

        addLike(filmB.getId(), u1);
        addLike(filmB.getId(), u2); // общий
        addLike(filmB.getId(), u3); // больше лайков (3)

        addLike(filmC.getId(), u1);
        addLike(filmC.getId(), u2); // общий

        // filmD только у u1
        addLike(filmD.getId(), u1);

        // Ожидаемый порядок: filmB (3 лайка), filmA (2 лайка + дополнительный от u3, но filmA имеет 3, filmB тоже 3, тогда порядок по id)
        // для проверки сортировки сделаем filmB более популярным, добавим ещё один лайк от нового пользователя u4
        long u4 = createTestUser("u4@mail.ru", "user4");
        addLike(filmB.getId(), u4); // теперь у filmB 4 лайка
        // Теперь filmB (4) > filmA (3) > filmC (2)

        List<Film> common = filmStorage.findCommonFilms(u1, u2);
        assertThat(common).hasSize(3);
        assertThat(common).extracting(Film::getId)
                .containsExactly(filmB.getId(), filmA.getId(), filmC.getId());

        assertThat(common).extracting(Film::getId).doesNotContain(filmD.getId());
    }

    @Test
    @DisplayName("Поиск общих фильмов: фильм лайкнут одним пользователем, но не другим")
    void findCommonFilms_WhenFilmLikedByOnlyOneUser_ShouldNotBeInResult() {
        Film film = filmStorage.create(createFilm("Film", "Desc", LocalDate.of(2000, 1, 1), 100, 1));
        long u1 = createTestUser("u1@mail.ru", "user1");
        long u2 = createTestUser("u2@mail.ru", "user2");
        addLike(film.getId(), u1);

        List<Film> common = filmStorage.findCommonFilms(u1, u2);
        assertThat(common).isEmpty();
    }

    @Test
    public void findDirectorFilms_WhenLikesSort_ShouldReturnInRightOrder() {
        long filmA = filmStorage.create(createFilm("A", "Desc A", LocalDate.of(2000, 1, 1), 100, 1)).getId();
        long filmB = filmStorage.create(createFilm("B", "Desc B", LocalDate.of(2001, 2, 2), 120, 2)).getId();
        long filmC = filmStorage.create(createFilm("C", "Desc C", LocalDate.of(2002, 3, 3), 90, 3)).getId();

        long u1 = createTestUser("u1@mail.ru", "user1");
        long u2 = createTestUser("u2@mail.ru", "user2");
        long u3 = createTestUser("u3@mail.ru", "user3");
        long u4 = createTestUser("u4@mail.ru", "user4");

        long d = createTestDirector("Director");

        addLike(filmA, u1);
        addLike(filmA, u2);
        addLike(filmB, u1);
        addLike(filmB, u2);
        addLike(filmB, u3);
        addLike(filmC, u4);

        addDirector(filmA, d);
        addDirector(filmB, d);
        addDirector(filmC, d);

        List<Film> result = filmStorage.findDirectorFilms(d, FilmSortType.LIKES);

        assertThat(result).hasSize(3);
        assertThat(result).extracting(Film::getId).containsExactly(filmB, filmA, filmC);
    }

    @Test
    public void findDirectorFilms_WhenYearSort_ShouldReturnInRightOrder() {
        long filmA = filmStorage.create(createFilm("A", "Desc A", LocalDate.of(2000, 1, 1), 100, 1)).getId();
        long filmB = filmStorage.create(createFilm("B", "Desc B", LocalDate.of(2001, 2, 2), 120, 2)).getId();
        long filmC = filmStorage.create(createFilm("C", "Desc C", LocalDate.of(2002, 3, 3), 90, 3)).getId();

        long d = createTestDirector("Director");

        addDirector(filmA, d);
        addDirector(filmB, d);
        addDirector(filmC, d);

        List<Film> result = filmStorage.findDirectorFilms(d, FilmSortType.LIKES);

        assertThat(result).hasSize(3);
        assertThat(result).extracting(Film::getId).containsExactly(filmA, filmB, filmC);
    }
}