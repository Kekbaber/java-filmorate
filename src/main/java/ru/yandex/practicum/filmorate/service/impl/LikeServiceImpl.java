package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.LikeService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final LikeStorage storage;
    private final FilmService filmService;
    private final UserService userService;

    @Override
    public void add(long filmId, long userId) {
        log.info("Add like: filmId={}, userId={}", filmId, userId);
        log.debug("Checking existence of film {} and user {}", filmId, userId);
        filmService.findById(filmId);
        userService.findById(userId);
        storage.add(filmId, userId);
        log.debug("Like added successfully for filmId={}, userId={}", filmId, userId);
    }

    @Override
    public void remove(long filmId, long userId) {
        log.info("Remove like: filmId={}, userId={}", filmId, userId);
        log.debug("Checking existence of film {} and user {}", filmId, userId);
        filmService.findById(filmId);
        userService.findById(userId);
        if (!storage.findById(filmId).contains(userId)) {
            log.warn("Attempt to remove non-existing like: filmId={}, userId={}", filmId, userId);
        }
        storage.remove(filmId, userId);
        log.debug("Like removed successfully for filmId={}, userId={}", filmId, userId);
    }

    @Override
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