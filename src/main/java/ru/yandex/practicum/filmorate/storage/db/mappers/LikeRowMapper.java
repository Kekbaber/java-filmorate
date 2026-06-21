package ru.yandex.practicum.filmorate.storage.db.mappers;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Like;

import java.sql.ResultSet;
import java.sql.SQLException;

@Profile("database")
@Component
public class LikeRowMapper implements RowMapper<Like> {
    @Override
    public Like mapRow(ResultSet rs, int rowNum) throws SQLException {
        long filmId = rs.getLong("film_id");
        long userId = rs.getLong("user_id");
        return new Like(filmId, userId);
    }
}
