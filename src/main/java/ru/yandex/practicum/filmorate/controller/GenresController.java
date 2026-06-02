package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Collection;

@RestController
@RequestMapping("/genres")
@Slf4j
@RequiredArgsConstructor
public class GenresController {

    private final GenreService genreService;

    @GetMapping
    public Collection<Genre> getAllGenres() {
        log.info("GET /genres");
        return genreService.findAll();
    }

    @GetMapping("/{id}")
    public Genre getGenre(@PathVariable long id) {
        log.info("GET /genres/{}", id);
        return genreService.findById(id);
    }
}
