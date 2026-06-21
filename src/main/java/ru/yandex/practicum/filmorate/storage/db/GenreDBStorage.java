package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.db.queries.GenreQueries;

import java.util.*;

@Repository
@Profile("database")
@Slf4j
public class GenreDBStorage extends BaseStorage<Genre> implements GenreStorage {

    public GenreDBStorage(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Genre> findAll() {
        log.debug("DB: find all genres");
        return findMany(GenreQueries.FIND_ALL);
    }

    @Override
    public Optional<Genre> findById(long id) {
        log.debug("DB: find genre by id={}", id);
        return findOne(GenreQueries.FIND_BY_ID, id);
    }

    @Override
    public List<Genre> findGenresByFilmId(long filmId) {
        log.debug("DB: find genres for filmId={}", filmId);
        return findMany(GenreQueries.FIND_BY_FILM_ID, filmId);
    }

    @Override
    public void addGenresToFilm(long filmId, List<Long> genreIds) {
        log.debug("DB: add genres to filmId={}: {}", filmId, genreIds);
        jdbc.batchUpdate(GenreQueries.INSERT_FILM_GENRE, genreIds, genreIds.size(),
                (ps, genreId) -> {
                    ps.setLong(1, filmId);
                    ps.setLong(2, genreId);
                });
    }

    @Override
    public void deleteGenresFromFilm(long filmId) {
        log.debug("DB: delete genres from filmId={}", filmId);
        delete(GenreQueries.DELETE_FILM_GENRES, filmId);
    }

    @Override
    public Map<Long, List<Genre>> findGenresByFilmIds(Set<Long> filmIds) {
        log.debug("DB: find genres for {} filmIds", filmIds.size());
        if (filmIds == null || filmIds.isEmpty()) {
            return Map.of();
        }
        Map<Long, List<Genre>> result = new HashMap<>();
        namedJdbc.query(GenreQueries.FIND_BY_FILM_IDS, Map.of("ids", filmIds), rs -> {
            long filmId = rs.getLong("film_id");
            Genre genre = new Genre();
            genre.setId(rs.getLong("id"));
            genre.setName(rs.getString("name"));
            result.computeIfAbsent(filmId, k -> new ArrayList<>()).add(genre);
        });
        return result;
    }

    @Override
    public Set<Long> findExistingGenreIds(Set<Long> ids) {
        log.debug("DB: check existence of {} genreIds", ids.size());
        if (ids == null || ids.isEmpty()) {
            return Set.of();
        }
        return new HashSet<>(findLongs(GenreQueries.FIND_EXISTING_IDS, Map.of("ids", ids)));
    }
}
