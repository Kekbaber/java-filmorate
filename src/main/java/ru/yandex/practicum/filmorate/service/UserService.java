package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.util.IdGenerator;
import ru.yandex.practicum.filmorate.util.UserIdGenerator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserService {

    private final Map<Long, User> users = new HashMap<>();
    private final IdGenerator idGenerator;

    public UserService(UserIdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    public Collection<User> findAll() {
        log.info("Get users. Find {} users: {}", users.size(), users.values());
        return users.values();
    }

    public User create(User user) {
        long id = idGenerator.getNextId();
        user.setId(id);
        users.put(id, user);
        log.info("Posted user: id={}, login={}, Email={}", id, user.getLogin(), user.getEmail());
        return user;
    }

    public User update(User user) {
        long id = user.getId();
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пост с id = " + user.getId() + " не найден");
        }
        users.put(user.getId(), user);
        log.info("Updated user: id={}, login={}, Email={}", id, user.getLogin(), user.getEmail());
        return user;
    }
}
