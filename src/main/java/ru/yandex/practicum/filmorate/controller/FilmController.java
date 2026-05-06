package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.util.FilmIdGenerator;
import ru.yandex.practicum.filmorate.util.IdGenerator;
import ru.yandex.practicum.filmorate.validation.OnUpdate;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();
    private final IdGenerator idGenerator;

    public FilmController(FilmIdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Get films. Find {} films: {}", films.size(), films.values());
        return films.values();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film create(@Valid @RequestBody Film film) {
        long id = idGenerator.getNextId();
        film.setId(id);
        films.put(id, film);
        log.info("Posted film: id={}, name={}, releaseDate={}", id, film.getName(), film.getReleaseDate());
        return film;
    }

    @PutMapping
    public Film update(@Validated(OnUpdate.class) @RequestBody Film film) {
        long id = film.getId();
        if (!films.containsKey(id)) {
            throw new NotFoundException("Пост с id = " + film.getId() + " не найден");
        }
        films.put(film.getId(), film);
        log.info("Updated film: id={}, name={}, releaseDate={}", id, film.getName(), film.getReleaseDate());
        return film;
    }
}
