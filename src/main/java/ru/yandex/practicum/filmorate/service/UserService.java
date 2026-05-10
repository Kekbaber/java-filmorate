package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.impl.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Service
@Slf4j
public class UserService {

    private final InMemoryUserStorage storage;

    public UserService(InMemoryUserStorage storage) {
        this.storage = storage;
    }

    public Collection<User> findAll() {
        return storage.findAll();
    }

    public User findById(long id) {
        return storage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + id + " не найден"));
    }

    public User create(@Valid User user) {
        return storage.create(user);
    }

    public User update(User user) {
        return storage.update(user);
    }

    public User delete(long id) {
        return storage.delete(id);
    }
}
