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

    @Override
    public Collection<Film> findAll() {
        log.debug("Get all films from storage, size={}", films.size());
        return films.values();
    }

    @Override
    public Optional<Film> findById(long id) {
        log.debug("Find film by id={} in storage", id);
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public Film create(Film film) {
        long id = idGenerator.getNextId();
        film.setId(id);
        films.put(id, film);
        log.info("Storage: added film id={}, name={}, releaseDate={}", id, film.getName(), film.getReleaseDate());
        return film;
    }

    @Override
    public Film update(Film film) {
        long id = film.getId();
        if (!films.containsKey(id)) {
            log.warn("Attempt to update non-existing film id={}", id);
            throw new NotFoundException("Фильм с id=" + id + " не найден");
        }
        films.put(id, film);
        log.info("Storage: updated film id={}, name={}", id, film.getName());
        return film;
    }

    @Override
    public void delete(long id) {
        Film film = films.get(id);
        if (film == null) {
            log.warn("Attempt to delete non-existing film id={}", id);
            return; // или можно выбросить исключение, но обычно delete идемпотентен
        }
        log.info("Storage: removed film id={}, name={}", id, film.getName());
        films.remove(id);
    }
}
