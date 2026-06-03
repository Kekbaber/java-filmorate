package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.db.queries.UserQueries;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@Profile("database")
@Slf4j
public class UserDBStorage extends BaseStorage<User> implements UserStorage {

    public UserDBStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<User> findAll() {
        log.debug("DB: find all users");
        return findMany(UserQueries.FIND_ALL_USERS);
    }

    @Override
    public Optional<User> findById(long id) {
        log.debug("DB: find user by id={}", id);
        return findOne(UserQueries.FIND_USER_BY_ID, id);
    }

    @Override
    public User create(User user) {
        long id = insert(UserQueries.INSERT_USER,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday());
        user.setId(id);
        log.debug("DB: created user id={}", id);
        return user;
    }

    @Override
    public User update(User user) {
        update(UserQueries.UPDATE_USER,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
        log.debug("DB: updated user id={}", user.getId());
        return user;
    }

    @Override
    public void delete(long id) {
        delete(UserQueries.DELETE_USER, id);
        log.debug("DB: deleted user id={}", id);
    }

    @Override
    public List<User> findAllByIds(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            log.debug("DB: find users by ids - empty list");
            return List.of();
        }
        MapSqlParameterSource params = new MapSqlParameterSource("ids", ids);
        List<User> users = namedJdbc.query(UserQueries.FIND_USERS_BY_IDS, params, mapper);
        log.debug("DB: found {} users by ids", users.size());
        return users;
    }
}
