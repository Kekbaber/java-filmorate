package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.db.queries.FilmQueries;

import java.util.List;
import java.util.Optional;

@Repository
@Profile("database")
@Slf4j
public class FilmDBStorage extends BaseStorage<Film> implements FilmStorage {

    public FilmDBStorage(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Film> findAll() {
        return findMany(FilmQueries.FIND_ALL);
    }

    @Override
    public Optional<Film> findById(long id) {
        return findOne(FilmQueries.FIND_BY_ID, id);
    }

    @Override
    public Film create(Film film) {
        long id = insert(FilmQueries.INSERT_FILM,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpaId()
        );
        film.setId(id);

        return film;
    }

    @Override
    public Film update(Film film) {
        update(FilmQueries.UPDATE_FILM,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpaId(),
                film.getId()
        );
        return film;
    }

    @Override
    public void delete(long id) {
        delete(FilmQueries.DELETE_FILM, id);
    }

    @Override
    public List<Film> findPopularFilms(long limit) {
        return findMany(FilmQueries.FIND_POPULAR_FILMS, limit);
    }
}
