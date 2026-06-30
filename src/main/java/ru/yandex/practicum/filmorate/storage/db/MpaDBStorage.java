package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.db.queries.MpaQueries;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@Profile("database")
@Slf4j
public class MpaDBStorage extends BaseStorage<Mpa> implements MpaStorage {

    public MpaDBStorage(JdbcTemplate jdbc, RowMapper<Mpa> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Mpa> findAll() {
        log.debug("DB: find all MPA ratings");
        return findMany(MpaQueries.FIND_ALL);
    }

    @Override
    public Optional<Mpa> findById(long id) {
        log.debug("DB: find MPA rating by id={}", id);
        return findOne(MpaQueries.FIND_BY_ID, id);
    }

    @Override
    public Map<Long, Mpa> findAllByIds(Set<Long> ids) {
        log.debug("DB: find MPA ratings by ids: {}", ids);
        if (ids == null || ids.isEmpty()) {
            return Map.of();
        }
        return findMany(MpaQueries.FIND_BY_IDS, Map.of("ids", ids))
                .stream()
                .collect(Collectors.toMap(Mpa::getId, mpa -> mpa));
    }
}