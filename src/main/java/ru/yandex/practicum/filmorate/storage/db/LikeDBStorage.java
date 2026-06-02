package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.LikeStorage;
import ru.yandex.practicum.filmorate.storage.db.queries.LikeQueries;

import java.util.Set;
import java.util.stream.Collectors;

@Repository
@Profile("database")
@Slf4j
public class LikeDBStorage extends BaseStorage<Like> implements LikeStorage {

    public LikeDBStorage(JdbcTemplate jdbc, RowMapper<Like> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Set<Long> findUserIdsByFilmId(long filmId) {
        return jdbc.query(LikeQueries.FIND_BY_ID,
                        (rs, rowNum) -> rs.getLong("user_id"), filmId)
                .stream()
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public void addLike(long filmId, long userId) {
        executeUpdate(LikeQueries.INSERT_LIKE, filmId, userId);
    }

    @Override
    public void delete(long filmId, long userId) {
        delete(LikeQueries.DELETE_LIKE, filmId, userId);
    }
}
