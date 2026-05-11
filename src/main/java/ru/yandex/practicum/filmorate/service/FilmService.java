package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmService {
    public Collection<Film> findAll();

    public Film findById(long id);

    public Film create(Film film);

    public Film update(Film film);

    public void delete(long id);
}
