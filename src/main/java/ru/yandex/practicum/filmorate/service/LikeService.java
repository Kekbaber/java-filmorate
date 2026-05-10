package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
public class LikeService {

    private final LikeStorage storage;
    private final FilmService filmService;
    private final UserService userService;

    public void add(long filmId, long userId) {
        log.info("Add like: filmId={}, userId={}", filmId, userId);
        log.debug("Checking existence of film {} and user {}", filmId, userId);
        filmService.findById(filmId);
        userService.findById(userId);
        storage.add(filmId, userId);
        log.debug("Like added successfully for filmId={}, userId={}", filmId, userId);
    }

    public void remove(long filmId, long userId) {
        log.info("Remove like: filmId={}, userId={}", filmId, userId);
        log.debug("Checking existence of film {} and user {}", filmId, userId);
        filmService.findById(filmId);
        userService.findById(userId);
        storage.remove(filmId, userId);
        log.debug("Like removed successfully for filmId={}, userId={}", filmId, userId);
    }

    public Collection<Film> getPopular(long count) {
        log.debug("Get popular films, count={}", count);
        Collection<Long> topFilmIds = storage.getMostLikedFilmIds(count);
        log.debug("Found top film ids: {}", topFilmIds);
        Collection<Film> films = topFilmIds.stream()
                .map(filmService::findById)
                .toList();
        log.debug("Returned {} popular films", films.size());
        return films;
    }
}