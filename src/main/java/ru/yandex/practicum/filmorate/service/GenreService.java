package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreService {
    List<Genre> findAll();

    Genre findById(long id);

    List<Genre> findGenresForFilm(long filmId);

    void updateFilmGenres(long filmId, List<Genre> genres);
}
