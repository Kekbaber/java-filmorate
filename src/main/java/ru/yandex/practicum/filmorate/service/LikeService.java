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
        filmService.findById(filmId);
        userService.findById(userId);
        storage.add(filmId, userId);
    }

    public void remove(long filmId, long userId) {
        filmService.findById(filmId);
        userService.findById(userId);
        storage.remove(filmId, userId);
    }

    public Collection<Film> getPopular(long count) {
        Collection<Long> topFilmIds = storage.getMostLikedFilmIds(count);
        return topFilmIds.stream()
                .map(filmService::findById)
                .toList();
    }
}
