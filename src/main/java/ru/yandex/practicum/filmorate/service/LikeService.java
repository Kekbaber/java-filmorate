package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface LikeService {
    public void add(long filmId, long userId);

    public void remove(long filmId, long userId);

    public Collection<Film> getPopular(long count);
}
