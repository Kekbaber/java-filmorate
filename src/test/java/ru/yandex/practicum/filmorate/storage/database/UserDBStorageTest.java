package ru.yandex.practicum.filmorate.storage.database;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.db.UserDBStorage;
import ru.yandex.practicum.filmorate.storage.db.mappers.UserRowMapper;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDBStorage.class, UserRowMapper.class})
class UserDBStorageTest {

    @Autowired
    private final UserStorage userStorage;

    @Autowired
    private final JdbcTemplate jdbc;

    private User create(String email, String login, String name, LocalDate birthday) {
        User user = new User();
        user.setEmail(email);
        user.setLogin(login);
        user.setName(name);
        user.setBirthday(birthday);
        return user;
    }

    @BeforeEach
    void cleanTable() {
        jdbc.execute("DELETE FROM users");
        jdbc.execute("ALTER TABLE users ALTER COLUMN id RESTART WITH 1");
    }

   @Test
    void findAll_WhenEmpty_ShouldReturnEmpty() {
        Collection<User> users = userStorage.findAll();
        assertThat(users).isEmpty();
    }

    @Test
    void findById_WhenNotFound_ShouldReturnEmpty() {
        Optional<User> user = userStorage.findById(999L);
        assertThat(user).isEmpty();
    }

    @Test
    void create_ShouldGenerateIdAndSave() {
        User newUser = create("new@example.com", "newbie", "Newbie", LocalDate.of(1990, 1, 1));

        User created = userStorage.create(newUser);

        assertThat(created.getId()).isNotNull();
        Optional<User> fetched = userStorage.findById(created.getId());
        assertThat(fetched).isPresent();
        assertThat(fetched.get().getEmail()).isEqualTo("new@example.com");
        assertThat(fetched.get().getLogin()).isEqualTo("newbie");
    }

    @Test
    void findAll_ShouldReturnAllSaved() {
        User user1 = userStorage.create(create("u1@mail.ru", "user1", "First", LocalDate.of(2000, 1, 1)));
        User user2 = userStorage.create(create("u2@mail.ru", "user2", "Second", LocalDate.of(2000, 2, 2)));

        Collection<User> users = userStorage.findAll();

        assertThat(users).hasSize(2);
        assertThat(users).extracting(User::getId).containsExactlyInAnyOrder(user1.getId(), user2.getId());
    }

    @Test
    void findById_WhenExists_ShouldReturn() {
        User saved = userStorage.create(create("find@mail.ru", "findMe", "Findable", LocalDate.of(1995, 5, 5)));

        Optional<User> found = userStorage.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getLogin()).isEqualTo("findMe");
        assertThat(found.get().getEmail()).isEqualTo("find@mail.ru");
    }

    @Test
    void update_ShouldChangeFields() {
        User original = userStorage.create(create("old@mail.ru", "old_login", "Old Name", LocalDate.of(2000, 1, 1)));
        Long id = original.getId();

        original.setEmail("new@mail.ru");
        original.setLogin("new_login");
        original.setName("New Name");
        original.setBirthday(LocalDate.of(2001, 2, 2));
        userStorage.update(original);

        Optional<User> updated = userStorage.findById(id);
        assertThat(updated).isPresent();
        assertThat(updated.get())
                .hasFieldOrPropertyWithValue("email", "new@mail.ru")
                .hasFieldOrPropertyWithValue("login", "new_login")
                .hasFieldOrPropertyWithValue("name", "New Name")
                .hasFieldOrPropertyWithValue("birthday", LocalDate.of(2001, 2, 2));
    }

    @Test
    void delete_ShouldRemove() {
        User toDelete = userStorage.create(create("delete@mail.ru", "deleteme", "ToBeDeleted", LocalDate.of(1999, 9, 9)));
        Long id = toDelete.getId();

        userStorage.delete(id);

        Optional<User> deleted = userStorage.findById(id);
        assertThat(deleted).isEmpty();
    }

    @Test
    void findAllByIds_WhenEmptyIds_ShouldReturnEmptyList() {
        List<User> users = userStorage.findAllByIds(List.of());
        assertThat(users).isEmpty();
    }

    @Test
    void findAllByIds_WhenNullIds_ShouldReturnEmptyList() {
        List<User> users = userStorage.findAllByIds(null);
        assertThat(users).isEmpty();
    }

    @Test
    void findAllByIds_WhenSomeIds_ShouldReturnMatchingUsers() {
        User user1 = userStorage.create(create("u1@mail.ru", "user1", "User One", LocalDate.of(2000, 1, 1)));
        User user2 = userStorage.create(create("u2@mail.ru", "user2", "User Two", LocalDate.of(2000, 2, 2)));
        userStorage.create(create("u3@mail.ru", "user3", "User Three", LocalDate.of(2000, 3, 3)));

        List<User> users = userStorage.findAllByIds(List.of(user1.getId(), user2.getId()));
        assertThat(users).hasSize(2);
        assertThat(users).extracting(User::getId).containsExactlyInAnyOrder(user1.getId(), user2.getId());
    }

    @Test
    void findAllByIds_WhenIdsNotExist_ShouldReturnEmptyList() {
        userStorage.create(create("existing@mail.ru", "existing", "Existing", LocalDate.of(2000, 1, 1)));

        List<User> users = userStorage.findAllByIds(List.of(999L, 888L));
        assertThat(users).isEmpty();
    }
}