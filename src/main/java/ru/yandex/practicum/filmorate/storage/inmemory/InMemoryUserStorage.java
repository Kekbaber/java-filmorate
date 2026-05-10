package ru.yandex.practicum.filmorate.storage.inmemory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.util.IdGenerator;
import ru.yandex.practicum.filmorate.util.UserIdGenerator;

import java.util.*;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private final IdGenerator idGenerator;

    public InMemoryUserStorage(UserIdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    public Collection<User> findAll() {
        log.debug("Get all users from storage, size={}", users.size());
        return users.values();
    }

    @Override
    public Optional<User> findById(long id) {
        log.debug("Find user by id={} in storage", id);
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User create(User user) {
        long id = idGenerator.getNextId();
        user.setId(id);
        users.put(id, user);
        log.info("Storage: added user id={}, login={}", id, user.getLogin());
        return user;
    }

    @Override
    public User update(User user) {
        long id = user.getId();
        if (!users.containsKey(id)) {
            log.warn("Attempt to update non-existing user id={}", id);
            throw new NotFoundException("Пользователь с id = " + user.getId() + " не найден");
        }
        users.put(user.getId(), user);
        log.info("Storage: updated user id={}, login={}", id, user.getLogin());
        return user;
    }

    @Override
    public void delete(long id) {
        User user = users.get(id);
        if (user == null) {
            log.warn("Attempt to delete non-existing user id={}", id);
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        log.info("Storage: removed user id={}, login={}", id, user.getLogin());
        users.remove(id);
    }
}
