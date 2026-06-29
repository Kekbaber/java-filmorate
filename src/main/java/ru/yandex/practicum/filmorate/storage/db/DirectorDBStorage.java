package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.db.queries.DirectorQueries;

import java.util.*;

@Repository
@Profile("database")
@Slf4j
public class DirectorDBStorage extends BaseStorage<Director> implements DirectorStorage {

    public DirectorDBStorage(JdbcTemplate jdbc, RowMapper<Director> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Director> findAll() {
        log.debug("DB: find all directors");
        return findMany(DirectorQueries.FIND_ALL);
    }

    @Override
    public Optional<Director> findById(long id) {
        log.debug("DB: find director by id={}", id);
        return findOne(DirectorQueries.FIND_BY_ID, id);
    }

    @Override
    public Director create(Director director) {
        long id = insert(DirectorQueries.INSERT_DIRECTOR, director.getName());
        director.setId(id);
        log.debug("DB: created director id={}", id);
        return director;
    }

    @Override
    public Director update(Director director) {
        update(DirectorQueries.UPDATE_DIRECTOR, director.getName(), director.getId());
        log.debug("DB: updated director id={}", director.getId());
        return director;
    }

    @Override
    public void delete(long id) {
        delete(DirectorQueries.DELETE_DIRECTOR, id);
        log.debug("DB: deleted director id={}", id);
    }

    @Override
    public Map<Long, List<Director>> findByFilmIds(Set<Long> filmIds) {
        log.debug("DB: find directors for {} filmIds", filmIds.size());
        if (filmIds == null || filmIds.isEmpty()) {
            return Map.of();
        }
        Map<Long, List<Director>> result = new HashMap<>();
        namedJdbc.query(DirectorQueries.FIND_BY_FILM_IDS, Map.of("ids", filmIds), rs -> {
            long filmId = rs.getLong("film_id");
            Director director = mapper.mapRow(rs, rs.getRow());
            result.computeIfAbsent(filmId, k -> new ArrayList<>()).add(director);
        });
        return result;
    }

    @Override
    public Set<Long> findExistingIds(Set<Long> ids) {
        log.debug("DB: check existence of {} directorIds", ids.size());
        if (ids == null || ids.isEmpty()) {
            return Set.of();
        }
        return new HashSet<>(findLongs(DirectorQueries.FIND_EXISTING_IDS, Map.of("ids", ids)));
    }

    @Override
    public void deleteDirectorsFromFilm(long filmId) {
        log.debug("DB: delete directors from filmId={}", filmId);
        delete(DirectorQueries.DELETE_FILM_DIRECTORS, filmId);
    }

    @Override
    public void addDirectorsToFilm(long filmId, List<Long> directorIds) {
        log.debug("DB: add directors to filmId={}: {}", filmId, directorIds);
        jdbc.batchUpdate(DirectorQueries.ADD_DIRECTOR_TO_FILM, directorIds, directorIds.size(),
                (ps, directorId) -> {
                    ps.setLong(1, filmId);
                    ps.setLong(2, directorId);
                });
    }
}
