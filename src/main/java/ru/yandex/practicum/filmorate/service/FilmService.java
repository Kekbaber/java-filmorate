package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.impl.InMemoryFilmStorage;

import java.util.Collection;

@Service
@Slf4j
public class FilmService {

    private final FilmStorage storage;

    public FilmService(InMemoryFilmStorage storage) {
        this.storage = storage;
    }

    public Collection<Film> findAll() {
        return storage.findAll();
    }

    public Film findById(long id) {
        return storage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + id + " не найден"));
    }

    public Film create(Film film) {
        return storage.create(film);
    }

    public Film update(Film film) {
        return storage.update(film);
    }

    public void delete(long id) {
        storage.delete(id);
    }
}
