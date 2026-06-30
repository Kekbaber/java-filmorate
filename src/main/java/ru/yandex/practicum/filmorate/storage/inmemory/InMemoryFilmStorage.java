package ru.yandex.practicum.filmorate.storage.inmemory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;
import ru.yandex.practicum.filmorate.storage.inmemory.id.IdGenerator;
import ru.yandex.practicum.filmorate.storage.inmemory.id.impl.FilmIdGenerator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Profile("inmemory")
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private final IdGenerator idGenerator;
    private final LikeStorage likeStorage;

    public InMemoryFilmStorage(FilmIdGenerator idGenerator, LikeStorage likeStorage) {
        this.idGenerator = idGenerator;
        this.likeStorage = likeStorage;
    }

    @Override
    public List<Film> findAll() {
        log.debug("Get all films from storage, size={}", films.size());
        return films.values().stream().toList();
    }

    @Override
    public Optional<Film> findById(long id) {
        log.debug("Find film by id={} in storage", id);
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public Film create(Film film) {
        long id = idGenerator.getNextId();
        film.setId(id);
        films.put(id, film);
        log.debug("Storage: added film id={}, name={}, releaseDate={}", id, film.getName(), film.getReleaseDate());
        return film;
    }

    @Override
    public Film update(Film film) {
        long id = film.getId();
        films.put(id, film);
        log.debug("Storage: updated film id={}, name={}", id, film.getName());
        return film;
    }

    @Override
    public void delete(long id) {
        Film film = films.get(id);
        log.debug("Storage: removed film id={}, name={}", id, film.getName());
        films.remove(id);
    }

    @Override
    public List<Film> findPopularFilms(long limit, Long genreId, Integer year) {
        log.debug("Find popular films, limit={}", limit);
        return films.values().stream()
                .sorted((f1, f2) -> {
                    int likes1 = likeStorage.findUserIdsByFilmId(f1.getId()).size();
                    int likes2 = likeStorage.findUserIdsByFilmId(f2.getId()).size();
                    return Integer.compare(likes2, likes1); // descending order
                })
                .limit(limit)
                .toList();
    }
}
