package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.util.IdGenerator;
import ru.yandex.practicum.filmorate.util.UserIdGenerator;
import ru.yandex.practicum.filmorate.validation.OnUpdate;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final Map<Long, User> users = new HashMap<>();
    private final IdGenerator idGenerator;

    public UserController(UserIdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @GetMapping
    public Collection<User> findAll() {
        log.info("Get users. Find {} users: {}", users.size(), users.values());
        return users.values();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@Valid @RequestBody User user) {
        long id = idGenerator.getNextId();
        user.setId(id);
        users.put(id, user);
        log.info("Posted user: id={}, login={}, Email={}", id, user.getLogin(), user.getEmail());
        return user;
    }

    @PutMapping
    public User update(@Validated(OnUpdate.class) @RequestBody User user) {
        long id = user.getId();
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пост с id = " + user.getId() + " не найден");
        }
        users.put(user.getId(), user);
        log.info("Updated user: id={}, login={}, Email={}", id, user.getLogin(), user.getEmail());
        return user;
    }
}
