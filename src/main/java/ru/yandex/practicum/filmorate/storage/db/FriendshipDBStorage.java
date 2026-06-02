package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.db.queries.FriendshipQueries;

import java.util.HashSet;
import java.util.Set;

@Repository
@Profile("database")
@Slf4j
public class FriendshipDBStorage extends BaseStorage<Friendship> implements FriendshipStorage {

    public FriendshipDBStorage(JdbcTemplate jdbc, RowMapper<Friendship> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Set<Long> findConfirmedFriendIds(long userId) {
        log.debug("DB: find confirmed friends of userId={}", userId);
        return new HashSet<>(jdbc.query(FriendshipQueries.FIND_CONFIRMED_FRIENDS,
                (rs, rowNum) -> rs.getLong("friend_id"), userId));
    }

    @Override
    public Set<Long> findOutgoingRequests(long userId) {
        log.debug("DB: find outgoing requests of userId={}", userId);
        return new HashSet<>(jdbc.query(FriendshipQueries.FIND_OUTGOING_REQUESTS,
                (rs, rowNum) -> rs.getLong("friend_id"), userId));
    }

    @Override
    public Set<Long> findIncomingRequests(long userId) {
        log.debug("DB: find incoming requests for userId={}", userId);
        return new HashSet<>(jdbc.query(FriendshipQueries.FIND_INCOMING_REQUESTS,
                (rs, rowNum) -> rs.getLong("user_id"), userId));
    }

    @Override
    public void addFriendRequest(long userId, long friendId, boolean confirmed) {
        log.debug("DB: add friend request userId={}, friendId={}, confirmed={}", userId, friendId, confirmed);
        jdbc.update(FriendshipQueries.ADD_REQUEST, userId, friendId, confirmed);
    }

    @Override
    public void deleteFriendship(long userId, long friendId) {
        log.debug("DB: delete friendship userId={}, friendId={}", userId, friendId);
        jdbc.update(FriendshipQueries.REMOVE_FRIENDSHIP, userId, friendId);
    }
}
