package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserService {
    public Collection<User> findAll();

    public User findById(long id);

    public User create(User user);

    public User update(User user);

    public void delete(long id);
}
