package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface GenreService {
    List<Genre> findAll();

    Genre findById(long id);

    List<Genre> findGenresForFilm(long filmId);

    void updateFilmGenres(long filmId, List<Genre> genres);

    Map<Long, List<Genre>> findGenresByFilmIds(Set<Long> filmIds);
}
