package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.db.queries.GenreQueries;

import java.util.List;
import java.util.Optional;

@Repository
@Profile("database")
@Slf4j
public class GenreDBStorage extends BaseStorage<Genre> implements GenreStorage {

    public GenreDBStorage(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Genre> findAll() {
        return findMany(GenreQueries.FIND_ALL);
    }

    @Override
    public Optional<Genre> findById(long id) {
        return findOne(GenreQueries.FIND_BY_ID, id);
    }

    @Override
    public List<Genre> findGenresByFilmId(long filmId) {
        return findMany(GenreQueries.FIND_BY_FILM_ID, filmId);
    }

    @Override
    public void addGenresToFilm(long filmId, List<Long> genreIds) {
        jdbc.batchUpdate(GenreQueries.INSERT_FILM_GENRE, genreIds, genreIds.size(),
                (ps, genreId) -> {
                    ps.setLong(1, filmId);
                    ps.setLong(2, genreId);
                });
    }

    @Override
    public void deleteGenresFromFilm(long filmId) {
        delete(GenreQueries.DELETE_FILM_GENRES, filmId);
    }
}
