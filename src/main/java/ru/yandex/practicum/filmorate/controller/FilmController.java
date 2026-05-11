package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.impl.FilmServiceImpl;
import ru.yandex.practicum.filmorate.service.impl.LikeServiceImpl;
import ru.yandex.practicum.filmorate.validation.OnUpdate;

import java.util.Collection;

@RestController
@RequestMapping("/films")
@Slf4j
@RequiredArgsConstructor
public class FilmController {

    private final FilmServiceImpl filmService;
    private final LikeServiceImpl likeService;

    @GetMapping
    public Collection<Film> findAll() {
        log.debug("GET /films");
        Collection<Film> films = filmService.findAll();
        log.debug("GET /films -> returned {} films", films.size());
        return films;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film create(@Valid @RequestBody Film film) {
        log.info("POST /films: {}", film.getName());
        Film created = filmService.create(film);
        log.info("Created film with id={}", created.getId());
        return created;
    }

    @PutMapping
    public Film update(@Validated(OnUpdate.class) @RequestBody Film film) {
        log.info("PUT /films: id={}", film.getId());
        Film updated = filmService.update(film);
        log.info("Updated film with id={}", updated.getId());
        return updated;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive long id) {
        log.info("DELETE /films/{}", id);
        filmService.delete(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(
            @PathVariable(name = "id") @Positive long filmId,
            @PathVariable @Positive long userId
    ) {
        log.info("PUT films/{}/like/{} - add like", filmId, userId);
        likeService.add(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(
            @PathVariable(name = "id") @Positive long filmId,
            @PathVariable @Positive long userId
    ) {
        log.info("DELETE films/{}/like/{} - remove like", filmId, userId);
        likeService.remove(filmId, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(@RequestParam(defaultValue = "10") @Positive long count) {
        log.debug("GET /films/popular?count={}", count);
        Collection<Film> popular = likeService.getPopular(count);
        log.debug("Returned {} popular films", popular.size());
        return popular;
    }
}
