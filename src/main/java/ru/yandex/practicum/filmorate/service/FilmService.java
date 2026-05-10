package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage storage;

    public Collection<Film> findAll() {
        log.debug("Find all films");
        Collection<Film> films = storage.findAll();
        log.debug("Found {} films", films.size());
        return films;
    }

    public Film findById(long id) {
        log.debug("Find film by id={}", id);
        // Исключение будет обработано глобально, логировать здесь не обязательно
        return storage.findById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + id + " не найден"));
    }

    public Film create(Film film) {
        log.info("Create film: name={}, releaseDate={}", film.getName(), film.getReleaseDate());
        Film created = storage.create(film);
        log.info("Created film with id={}", created.getId());
        return created;
    }

    public Film update(Film film) {
        log.info("Update film id={}, name={}", film.getId(), film.getName());
        Film updated = storage.update(film);
        log.info("Updated film id={}", updated.getId());
        return updated;
    }

    public void delete(long id) {
        log.info("Delete film id={}", id);
        storage.delete(id);
        log.info("Deleted film id={}", id);
    }
}