package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.util.FilmIdGenerator;
import ru.yandex.practicum.filmorate.util.IdGenerator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class FilmService {

    private final Map<Long, Film> films = new HashMap<>();
    private final IdGenerator idGenerator;

    public FilmService(FilmIdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    public Collection<Film> findAll() {
        log.info("Get films. Find {} films: {}", films.size(), films.values());
        return films.values();
    }

    public Film create(Film film) {
        long id = idGenerator.getNextId();
        film.setId(id);
        films.put(id, film);
        log.info("Posted film: id={}, name={}, releaseDate={}", id, film.getName(), film.getReleaseDate());
        return film;
    }

    public Film update(Film film) {
        long id = film.getId();
        if (!films.containsKey(id)) {
            throw new NotFoundException("Пост с id = " + film.getId() + " не найден");
        }
        films.put(film.getId(), film);
        log.info("Updated film: id={}, name={}, releaseDate={}", id, film.getName(), film.getReleaseDate());
        return film;
    }
}
