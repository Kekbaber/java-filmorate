package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface DirectorStorage {
    List<Director> findAll();

    Optional<Director> findById(long id);

    Director create(Director director);

    Director update(Director director);

    void delete(long id);

    Map<Long, List<Director>> findByFilmIds(Set<Long> filmIds);

    Set<Long> findExistingIds(Set<Long> ids);

    void deleteDirectorsFromFilm(long filmId);

    void addDirectorsToFilm(long filmId, List<Long> directorIds);

    default void updateFilmDirectors(long filmId, List<Long> directorIds) {
        if (directorIds != null && !directorIds.isEmpty()) {
            deleteDirectorsFromFilm(filmId);
            addDirectorsToFilm(filmId, directorIds);
        }

    }
}
