package ru.yandex.practicum.filmorate.storage.db.mappers;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Friendship;

import java.sql.ResultSet;
import java.sql.SQLException;

@Profile("database")
@Component
public class FriendshipRowMapper implements RowMapper<Friendship> {
    @Override
    public Friendship mapRow(ResultSet rs, int rowNum) throws SQLException {
        long userId = rs.getLong("user_id");
        long friendId = rs.getLong("friend_id");
        boolean confirmed = rs.getBoolean("confirmed");
        return new Friendship(userId, friendId, confirmed);
    }
}
