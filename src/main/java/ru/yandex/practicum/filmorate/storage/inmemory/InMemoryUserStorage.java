package ru.yandex.practicum.filmorate.storage.inmemory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.inmemory.id.IdGenerator;
import ru.yandex.practicum.filmorate.storage.inmemory.id.impl.UserIdGenerator;

import java.util.*;

@Component
@Profile("inmemory")
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private final IdGenerator idGenerator;

    public InMemoryUserStorage(UserIdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    public List<User> findAll() {
        log.debug("Get all users from storage, size={}", users.size());
        return users.values().stream().toList();
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
        users.put(user.getId(), user);
        log.info("Storage: updated user id={}, login={}", id, user.getLogin());
        return user;
    }

    @Override
    public void delete(long id) {
        User user = users.get(id);
        log.info("Storage: removed user id={}, login={}", id, user.getLogin());
        users.remove(id);
    }

    @Override
    public List<User> findAllByIds(Collection<Long> ids) {
        return users.values().stream().filter(user -> ids.contains(user.getId())).toList();
    }
}
