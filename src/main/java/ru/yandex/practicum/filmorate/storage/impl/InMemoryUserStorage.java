package ru.yandex.practicum.filmorate.storage.impl;

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
        log.info("Get users. Find {} users: {}", users.size(), users.values());
        return users.values();
    }

    @Override
    public Optional<User> findById(long id) {
        log.info("Get user by id {}", id);
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User create(User user) {
        long id = idGenerator.getNextId();
        user.setId(id);
        users.put(id, user);
        log.info("Add user: id={}, login={}, Email={}", id, user.getLogin(), user.getEmail());
        return user;
    }

    @Override
    public User update(User user) {
        long id = user.getId();
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь с id = " + user.getId() + " не найден");
        }
        users.put(user.getId(), user);
        log.info("Update user: id={}, login={}, Email={}", id, user.getLogin(), user.getEmail());
        return user;
    }

    @Override
    public User delete(long id) {
        User user = users.get(id);
        log.info("Remove user: id={}, login={}, Email={}", user.getId(), user.getLogin(), user.getEmail());
        users.remove(id);
        return user;
    }
}
