package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.request.CreateFilmRequest;
import ru.yandex.practicum.filmorate.dto.request.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.dto.response.FilmResponse;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.LikeService;

import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;
    private final LikeService likeService;

    @GetMapping
    public List<FilmResponse> findAll() {
        log.debug("GET /films");
        List<FilmResponse> films = filmService.findAll();
        log.debug("GET /films -> returned {} films", films.size());
        return films;
    }

    @GetMapping("/{id}")
    public FilmResponse findFilmById(@PathVariable long id) {
        log.info("GET /films/{}", id);
        return filmService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FilmResponse create(@Valid @RequestBody CreateFilmRequest request) {
        log.info("POST /films: {}", request.getName());
        FilmResponse created = filmService.create(request);
        log.info("Created film with id={}", created.getId());
        return created;
    }

    @PutMapping
    public FilmResponse update(@Valid @RequestBody UpdateFilmRequest request) {
        log.info("PUT /films: id={}", request.getId());
        FilmResponse updated = filmService.update(request);
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
        likeService.addLike(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeLike(
            @PathVariable(name = "id") @Positive long filmId,
            @PathVariable @Positive long userId
    ) {
        log.info("DELETE films/{}/like/{} - remove like", filmId, userId);
        likeService.deleteLike(filmId, userId);
    }

    @GetMapping("/popular")
    public List<FilmResponse> getPopularFilms(
            @RequestParam(defaultValue = "10") @Positive long count,
            @RequestParam(required = false) Long genreId,
            @RequestParam(required = false) Integer year
            ) {
        log.debug("GET /films/popular?count={}&genreId={}&year={}", count, genreId, year);
        List<FilmResponse> popular = filmService.findPopularFilms(count, genreId, year);
        log.debug("Returned {} popular films", popular.size());
        return popular;
    }
}