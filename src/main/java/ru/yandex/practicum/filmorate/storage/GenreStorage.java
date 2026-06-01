package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreStorage {
    List<Genre> findAll();

    Optional<Genre> findById(long id);

    List<Genre> findGenresByFilmId(long filmId);

    void addGenresToFilm(long filmId, List<Long> genreIds);

    void deleteGenresFromFilm(long filmId);

    default void updateFilmGenres(long filmId, List<Long> newGenreIds) {
        if (newGenreIds != null && !newGenreIds.isEmpty()) {
            deleteGenresFromFilm(filmId);
            addGenresToFilm(filmId, newGenreIds);
        }
    }
}