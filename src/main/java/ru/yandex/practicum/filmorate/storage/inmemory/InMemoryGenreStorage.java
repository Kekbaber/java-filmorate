package ru.yandex.practicum.filmorate.storage.inmemory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.*;

@Component
@Profile("inmemory")
@Slf4j
public class InMemoryGenreStorage implements GenreStorage {

    private final Map<Long, Genre> genres = new HashMap<>();
    private final Map<Long, Set<Long>> filmGenres = new HashMap<>();

    public InMemoryGenreStorage() {
        initGenres();
    }

    private void initGenres() {
        // Предопределенные жанры в соответствии с БД (id и name)
        addGenre(1L, "Комедия");
        addGenre(2L, "Драма");
        addGenre(3L, "Мультфильм");
        addGenre(4L, "Триллер");
        addGenre(5L, "Документальный");
        addGenre(6L, "Боевик");
        log.debug("Initialized in-memory genres: {}", genres.size());
    }

    private void addGenre(long id, String name) {
        Genre genre = new Genre();
        genre.setId(id);
        genre.setName(name);
        genres.put(id, genre);
    }

    @Override
    public List<Genre> findAll() {
        log.debug("Get all genres from in-memory storage");
        return new ArrayList<>(genres.values());
    }

    @Override
    public Optional<Genre> findById(long id) {
        log.debug("Find genre by id={} in in-memory storage", id);
        return Optional.ofNullable(genres.get(id));
    }

    @Override
    public List<Genre> findGenresByFilmId(long filmId) {
        log.debug("Find genres for film id={} in in-memory storage", filmId);
        Set<Long> genreIds = filmGenres.getOrDefault(filmId, Collections.emptySet());
        return genreIds.stream()
                .map(genres::get)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingLong(Genre::getId))
                .toList();
    }

    @Override
    public void addGenresToFilm(long filmId, List<Long> genreIds) {
        if (genreIds == null || genreIds.isEmpty()) {
            log.debug("No genres to add for film id={}", filmId);
            return;
        }
        Set<Long> currentGenres = filmGenres.computeIfAbsent(filmId, k -> new HashSet<>());
        for (Long genreId : genreIds) {
            if (genres.containsKey(genreId)) {
                currentGenres.add(genreId);
            } else {
                log.warn("Genre id={} not found, skipped for film id={}", genreId, filmId);
            }
        }
        log.debug("Added genres {} to film id={}", genreIds, filmId);
    }

    @Override
    public void deleteGenresFromFilm(long filmId) {
        Set<Long> removed = filmGenres.remove(filmId);
        if (removed != null) {
            log.debug("Deleted all genres for film id={}, removed genres: {}", filmId, removed);
        } else {
            log.debug("No genres to delete for film id={}", filmId);
        }
    }
}
