package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface GenreStorage {
    List<Genre> findAll();

    Optional<Genre> findById(long id);

    List<Genre> findGenresByFilmId(long filmId);

    void addGenresToFilm(long filmId, List<Long> genreIds);

    void deleteGenresFromFilm(long filmId);

    Map<Long, List<Genre>> findGenresByFilmIds(Set<Long> filmIds);

    Set<Long> findExistingGenreIds(Set<Long> ids);

    default void updateFilmGenres(long filmId, List<Long> newGenreIds) {
        if (newGenreIds != null && !newGenreIds.isEmpty()) {
            deleteGenresFromFilm(filmId);
            addGenresToFilm(filmId, newGenreIds);
        }
    }
}