package ru.yandex.practicum.filmorate.storage.database;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.db.GenreDBStorage;
import ru.yandex.practicum.filmorate.storage.db.mappers.GenreRowMapper;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({GenreDBStorage.class, GenreRowMapper.class})
class GenreDBStorageTest {

    @Autowired
    private final GenreStorage genreStorage;

    @Autowired
    private final JdbcTemplate jdbc;

    @BeforeEach
    void cleanUp() {
        jdbc.execute("DELETE FROM film_genre");
        jdbc.execute("DELETE FROM films");
        jdbc.execute("ALTER TABLE films ALTER COLUMN id RESTART WITH 1");
    }

    private long createTestFilm(String name) {
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
    void findAll_ShouldReturnAllGenres() {
        Collection<Genre> genres = genreStorage.findAll();
        assertThat(genres).hasSize(6); // В H2 из скрипта вставляется 6 жанров
    }

    @Test
    void findAll_ShouldReturnCorrectGenresNames() {
        Collection<Genre> genres = genreStorage.findAll();
        List<String> names = genres.stream().map(Genre::getName).toList();
        assertThat(names).containsExactlyInAnyOrder(
                "Комедия", "Драма", "Мультфильм", "Триллер", "Документальный", "Боевик");
    }

    @Test
    void findById_WhenExists_ShouldReturnGenre() {
        Optional<Genre> genre = genreStorage.findById(1L);
        assertThat(genre).isPresent();
        assertThat(genre.get().getId()).isEqualTo(1L);
        assertThat(genre.get().getName()).isEqualTo("Комедия");
    }

    @Test
    void findById_WhenNotExists_ShouldReturnEmpty() {
        Optional<Genre> genre = genreStorage.findById(999L);
        assertThat(genre).isEmpty();

        Optional<Genre> negativeId = genreStorage.findById(-1L);
        assertThat(negativeId).isEmpty();
    }

    @Test
    void getGenresForFilm_WhenFilmHasGenres_ShouldReturnSortedGenres() {
        long filmId = createTestFilm("Film with genres");
        // Добавляем жанры с id 1, 3, 5 (комедия, мультфильм, документальный)
        genreStorage.addGenresToFilm(filmId, List.of(1L, 3L, 5L));

        List<Genre> genres = genreStorage.findGenresByFilmId(filmId);
        assertThat(genres).hasSize(3);
        assertThat(genres).extracting(Genre::getId).containsExactly(1L, 3L, 5L);
        assertThat(genres).extracting(Genre::getName).containsExactly("Комедия", "Мультфильм", "Документальный");
    }

    @Test
    void getGenresForFilm_WhenFilmHasNoGenres_ShouldReturnEmptyList() {
        long filmId = createTestFilm("Film without genres");
        List<Genre> genres = genreStorage.findGenresByFilmId(filmId);
        assertThat(genres).isEmpty();
    }

    @Test
    void addGenresToFilm_ShouldInsertGenres() {
        long filmId = createTestFilm("Film for addGenres");
        genreStorage.addGenresToFilm(filmId, List.of(2L, 4L, 6L)); // драма, триллер, боевик

        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM film_genre WHERE film_id = ?", Integer.class, filmId);
        assertThat(count).isEqualTo(3);

        List<Genre> genres = genreStorage.findGenresByFilmId(filmId);
        assertThat(genres).extracting(Genre::getId).containsExactly(2L, 4L, 6L);
    }

    @Test
    void addGenresToFilm_WhenDuplicateGenre_ShouldThrowDuplicateKeyException() {
        long filmId = createTestFilm("Film with duplicate genres");

        genreStorage.addGenresToFilm(filmId, List.of(1L));
        List<Long> duplicate = List.of(1L);
        assertThatThrownBy(() -> genreStorage.addGenresToFilm(filmId, duplicate))
                .isInstanceOf(DuplicateKeyException.class);
    }

    @Test
    void addGenresToFilm_WhenDuplicateGenreIdsWithinSameCall_ShouldThrowDuplicateKeyException() {
        long filmId = createTestFilm("Film with duplicate genre ids in one call");
        List<Long> duplicateIds = List.of(1L, 1L, 2L);
        assertThatThrownBy(() -> genreStorage.addGenresToFilm(filmId, duplicateIds))
                .isInstanceOf(DuplicateKeyException.class);

        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM film_genre WHERE film_id = ?", Integer.class, filmId);
        assertThat(count).isEqualTo(2);
    }

    @Test
    void removeAllGenresFromFilm_ShouldDeleteAllGenresForFilm() {
        long filmId = createTestFilm("Film for removal");
        genreStorage.addGenresToFilm(filmId, List.of(1L, 2L, 3L));
        // Проверяем, что жанры добавились
        assertThat(genreStorage.findGenresByFilmId(filmId)).hasSize(3);

        genreStorage.deleteGenresFromFilm(filmId);

        List<Genre> genres = genreStorage.findGenresByFilmId(filmId);
        assertThat(genres).isEmpty();

        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM film_genre WHERE film_id = ?", Integer.class, filmId);
        assertThat(count).isZero();
    }

    @Test
    void removeAllGenresFromFilm_WhenNoGenres_ShouldDoNothing() {
        long filmId = createTestFilm("Film without genres");
        // Никаких жанров не добавляли
        genreStorage.deleteGenresFromFilm(filmId);
        // Проверяем, что ничего не сломалось
        assertThat(genreStorage.findGenresByFilmId(filmId)).isEmpty();
    }

    @Test
    void updateFilmGenres_ShouldReplaceOldGenresWithNew() {
        long filmId = createTestFilm("Film for update");
        genreStorage.addGenresToFilm(filmId, List.of(1L, 2L, 3L));
        assertThat(genreStorage.findGenresByFilmId(filmId)).hasSize(3);

        genreStorage.updateFilmGenres(filmId, List.of(4L, 5L));

        List<Genre> genres = genreStorage.findGenresByFilmId(filmId);
        assertThat(genres).hasSize(2);
        assertThat(genres).extracting(Genre::getId).containsExactly(4L, 5L);
    }
}
