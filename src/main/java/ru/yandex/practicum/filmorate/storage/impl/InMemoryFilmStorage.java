package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.util.FilmIdGenerator;
import ru.yandex.practicum.filmorate.util.IdGenerator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private final IdGenerator idGenerator;

    public InMemoryFilmStorage(FilmIdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    public Collection<Film> findAll() {
        log.info("Find all films. Found={}", films.size());
        return films.values();
    }

    @Override
    public Optional<Film> findById(long id) {
        log.info("Find film by id={}", id);
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public Film create(Film film) {
        long id = idGenerator.getNextId();
        film.setId(id);
        films.put(id, film);
        log.info("Add film: id={}, name={}, releaseDate={}", id, film.getName(), film.getReleaseDate());
        return film;
    }

    @Override
    public Film update(Film film) {
        long id = film.getId();
        if (!films.containsKey(id)) {
            throw new NotFoundException("Пост с id = " + film.getId() + " не найден");
        }
        films.put(film.getId(), film);
        log.info("Update film: id={}, name={}, releaseDate={}", id, film.getName(), film.getReleaseDate());
        return film;
    }

    @Override
    public void delete(long id) {
        log.info("Remove film by id={}", id);
        films.remove(id);
    }

}
