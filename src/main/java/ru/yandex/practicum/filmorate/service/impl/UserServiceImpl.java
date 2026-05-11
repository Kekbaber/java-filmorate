package ru.yandex.practicum.filmorate.service.impl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage storage;

    public Collection<User> findAll() {
        log.debug("Find all users");
        Collection<User> users = storage.findAll();
        log.debug("Found {} users", users.size());
        return users;
    }

    public User findById(long id) {
        log.debug("Find user by id={}", id);
        return storage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + id + " не найден"));
    }

    public User create(@Valid User user) {
        log.info("Create user: login={}, email={}", user.getLogin(), user.getEmail());
        User created = storage.create(user);
        log.info("Created user with id={}", created.getId());
        return created;
    }

    public User update(User user) {
        log.info("Update user id={}, login={}", user.getId(), user.getLogin());
        if (storage.findById(user.getId()).isEmpty()) {
            log.warn("Attempt to update non-existing user id={}", user.getId());
            throw new NotFoundException("Пользователь с id = " + user.getId() + " не найден");
        }
        User updated = storage.update(user);
        log.info("Updated user id={}", updated.getId());
        return updated;
    }

    public void delete(long id) {
        log.info("Delete user id={}", id);
        if (storage.findById(id).isEmpty()) {
            log.warn("Attempt to delete non-existing user id={}", id);
        }
        storage.delete(id);
        log.info("Deleted user id={}", id);
    }
}
