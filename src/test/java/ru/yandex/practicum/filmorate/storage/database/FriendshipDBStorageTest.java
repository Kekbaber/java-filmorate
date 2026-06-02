package ru.yandex.practicum.filmorate.storage.database;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.db.FriendshipDBStorage;
import ru.yandex.practicum.filmorate.storage.db.mappers.FriendshipRowMapper;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FriendshipDBStorage.class, FriendshipRowMapper.class})
class FriendshipDBStorageTest {

    @Autowired
    private final FriendshipStorage friendshipStorage;

    @Autowired
    private final JdbcTemplate jdbc;

    private long createTestUser(String email, String login) {
        String sql = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, email);
            ps.setString(2, login);
            ps.setString(3, login);
            ps.setDate(4, java.sql.Date.valueOf(LocalDate.of(2000, 1, 1)));
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @BeforeEach
    void cleanTables() {
        jdbc.execute("DELETE FROM friendships");
        jdbc.execute("DELETE FROM users");
        jdbc.execute("ALTER TABLE users ALTER COLUMN id RESTART WITH 1");
    }

    @Test
    void addFriendRequest_ShouldAddUnconfirmedFriendRequest() {
        long user1 = createTestUser("u1@mail.ru", "user1");
        long user2 = createTestUser("u2@mail.ru", "user2");

        friendshipStorage.addFriendRequest(user1, user2, false);

        Set<Long> outgoing = friendshipStorage.findOutgoingRequests(user1);
        assertThat(outgoing).containsExactly(user2);

        Set<Long> incoming = friendshipStorage.findIncomingRequests(user2);
        assertThat(incoming).containsExactly(user1);
    }

    @Test
    void addFriendRequest_ShouldAddFriendConfirmedFriendship() {
        long user1 = createTestUser("u1@mail.ru", "user1");
        long user2 = createTestUser("u2@mail.ru", "user2");

        friendshipStorage.addFriendRequest(user1, user2, true);

        Set<Long> confirmed = friendshipStorage.findConfirmedFriendIds(user1);
        assertThat(confirmed).containsExactly(user2);

        // Подтверждённая дружба не должна быть в исходящих/входящих запросах
        assertThat(friendshipStorage.findOutgoingRequests(user1)).isEmpty();
        assertThat(friendshipStorage.findIncomingRequests(user2)).isEmpty();
    }

    @Test
    void addFriendRequest_WhenAlreadyExists_ShouldUpdateConfirmed() {
        long user1 = createTestUser("u1@mail.ru", "user1");
        long user2 = createTestUser("u2@mail.ru", "user2");

        // Сначала добавляем неподтверждённый запрос
        friendshipStorage.addFriendRequest(user1, user2, false);
        assertThat(friendshipStorage.findOutgoingRequests(user1)).containsExactly(user2);

        // Затем обновляем его на подтверждённый (MERGE обновит confirmed)
        friendshipStorage.addFriendRequest(user1, user2, true);

        // Дружба должна стать подтверждённой
        Set<Long> confirmed = friendshipStorage.findConfirmedFriendIds(user1);
        assertThat(confirmed).containsExactly(user2);
        assertThat(friendshipStorage.findOutgoingRequests(user1)).isEmpty();
    }

    @Test
    void addFriendRequest_WhenUserEqualsFriend_ShouldThrowConstraintViolation() {
        long user = createTestUser("self@mail.ru", "self");

        // Попытка добавить дружбу с самим собой нарушает CHECK (user_id <> friend_id)
        assertThatThrownBy(() -> friendshipStorage.addFriendRequest(user, user, false))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void addFriendRequest_WhenUserNotExists_ShouldThrowException() {
        long nonExistentUser = 999L;
        long existingUser = createTestUser("exist@mail.ru", "exist");

        assertThatThrownBy(() -> friendshipStorage.addFriendRequest(nonExistentUser, existingUser, false))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void findConfirmedFriends_WhenNoFriendIds_ShouldReturnEmptySet() {
        long user = createTestUser("alone@mail.ru", "alone");
        Set<Long> friends = friendshipStorage.findConfirmedFriendIds(user);
        assertThat(friends).isEmpty();
    }

    @Test
    void findConfirmedFriends_ShouldReturnOnlyConfirmedFriendIds() {
        long user1 = createTestUser("u1@mail.ru", "u1");
        long user2 = createTestUser("u2@mail.ru", "u2");
        long user3 = createTestUser("u3@mail.ru", "u3");

        // user1 -> user2 (подтверждён)
        friendshipStorage.addFriendRequest(user1, user2, true);
        // user1 -> user3 (неподтверждён)
        friendshipStorage.addFriendRequest(user1, user3, false);

        Set<Long> confirmed = friendshipStorage.findConfirmedFriendIds(user1);
        assertThat(confirmed).containsExactly(user2).doesNotContain(user3);
    }

    @Test
    void findOutgoingRequests_ShouldReturnOnlyUnconfirmedRequests() {
        long user1 = createTestUser("u1@mail.ru", "u1");
        long user2 = createTestUser("u2@mail.ru", "u2");
        long user3 = createTestUser("u3@mail.ru", "u3");

        friendshipStorage.addFriendRequest(user1, user2, false); // исходящий
        friendshipStorage.addFriendRequest(user1, user3, true);  // подтверждённый

        Set<Long> outgoing = friendshipStorage.findOutgoingRequests(user1);
        assertThat(outgoing).containsExactly(user2).doesNotContain(user3);
    }

    @Test
    void findOutgoingRequests_WhenNoOutgoing_ShouldReturnEmpty() {
        long user = createTestUser("noOut@mail.ru", "noOut");
        Set<Long> outgoing = friendshipStorage.findOutgoingRequests(user);
        assertThat(outgoing).isEmpty();
    }

    @Test
    void findIncomingRequests_ShouldReturnOnlyUnconfirmedRequestsWhereUserIsFriend() {
        long user1 = createTestUser("u1@mail.ru", "u1");
        long user2 = createTestUser("u2@mail.ru", "u2");
        long user3 = createTestUser("u3@mail.ru", "u3");

        // user2 -> user1 (неподтверждён) – входящий для user1
        friendshipStorage.addFriendRequest(user2, user1, false);
        // user3 -> user1 (подтверждён) – не должен быть входящим
        friendshipStorage.addFriendRequest(user3, user1, true);

        Set<Long> incoming = friendshipStorage.findIncomingRequests(user1);
        assertThat(incoming).containsExactly(user2).doesNotContain(user3);
    }

    @Test
    void findIncomingRequests_WhenNoIncoming_ShouldReturnEmpty() {
        long user = createTestUser("noInc@mail.ru", "noInc");
        Set<Long> incoming = friendshipStorage.findIncomingRequests(user);
        assertThat(incoming).isEmpty();
    }

    @Test
    void deleteFriendship_ShouldRemoveFriendship() {
        long user1 = createTestUser("u1@mail.ru", "u1");
        long user2 = createTestUser("u2@mail.ru", "u2");

        friendshipStorage.addFriendRequest(user1, user2, true);
        assertThat(friendshipStorage.findConfirmedFriendIds(user1)).containsExactly(user2);

        friendshipStorage.deleteFriendship(user1, user2);
        assertThat(friendshipStorage.findConfirmedFriendIds(user1)).isEmpty();
        // Также проверяем, что удалилась именно запись user1->user2, а не симметричная (её нет)
    }

    @Test
    void deleteFriendship_WhenNonExistent_ShouldDoNothing() {
        long user1 = createTestUser("u1@mail.ru", "u1");
        long user2 = createTestUser("u2@mail.ru", "u2");

        // Удаляем несуществующую дружбу – не должно быть исключения
        friendshipStorage.deleteFriendship(user1, user2);
        // Просто проверяем, что ничего не сломалось
        assertThat(friendshipStorage.findConfirmedFriendIds(user1)).isEmpty();
    }

    @Test
    void removeFriendship_ShouldRemoveOnlySpecifiedDirection() {
        long user1 = createTestUser("u1@mail.ru", "u1");
        long user2 = createTestUser("u2@mail.ru", "u2");

        // Добавляем две разные записи: user1->user2 (подтверждён) и user2->user1 (неподтверждён)
        friendshipStorage.addFriendRequest(user1, user2, true);
        friendshipStorage.addFriendRequest(user2, user1, false);

        // Удаляем user1->user2
        friendshipStorage.deleteFriendship(user1, user2);

        // user1 больше не должен видеть user2 как подтверждённого друга
        assertThat(friendshipStorage.findConfirmedFriendIds(user1)).isEmpty();
        // У user2 должен остаться неподтверждённый запрос к user1
        Set<Long> outgoingFromUser2 = friendshipStorage.findOutgoingRequests(user2);
        assertThat(outgoingFromUser2).containsExactly(user1);
    }

    @Test
    void fullFriendshipLifeCycle() {
        long alice = createTestUser("alice@mail.ru", "alice");
        long bob = createTestUser("bob@mail.ru", "bob");

        // 1. Alice отправляет запрос дружбы Бобу (неподтверждённый)
        friendshipStorage.addFriendRequest(alice, bob, false);
        assertThat(friendshipStorage.findOutgoingRequests(alice)).containsExactly(bob);
        assertThat(friendshipStorage.findIncomingRequests(bob)).containsExactly(alice);
        assertThat(friendshipStorage.findConfirmedFriendIds(alice)).isEmpty();
        assertThat(friendshipStorage.findConfirmedFriendIds(bob)).isEmpty();

        // 2. Bob подтверждает дружбу (обновляет запись)
        friendshipStorage.addFriendRequest(alice, bob, true);
        assertThat(friendshipStorage.findOutgoingRequests(alice)).isEmpty();
        assertThat(friendshipStorage.findIncomingRequests(bob)).isEmpty();
        assertThat(friendshipStorage.findConfirmedFriendIds(alice)).containsExactly(bob);
        assertThat(friendshipStorage.findConfirmedFriendIds(bob)).isEmpty(); // не симметрично, только alice->bob

        // 3. Alice удаляет дружбу
        friendshipStorage.deleteFriendship(alice, bob);
        assertThat(friendshipStorage.findConfirmedFriendIds(alice)).isEmpty();
    }
}