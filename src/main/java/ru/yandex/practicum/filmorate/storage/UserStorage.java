package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface UserStorage {
    public Collection<User> findAll();
    public Optional<User> findById(long id);
    public User create(User user);
    public User update(User user);
    public User delete(long id);
}
