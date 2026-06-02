package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.LikeService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

@Service
@Slf4j
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final LikeStorage likeStorage;
    private final FilmService filmService;
    private final UserService userService;

    @Override
    public void addLike(long filmId, long userId) {
        log.info("Add like: filmId={}, userId={}", filmId, userId);
        log.debug("Checking existence of film {} and user {}", filmId, userId);
        filmService.findById(filmId);
        userService.findById(userId);
        likeStorage.addLike(filmId, userId);
        log.debug("Like added successfully for filmId={}, userId={}", filmId, userId);
    }

    @Override
    public void deleteLike(long filmId, long userId) {
        log.info("Remove like: filmId={}, userId={}", filmId, userId);
        log.debug("Checking existence of film {} and user {}", filmId, userId);
        filmService.findById(filmId);
        userService.findById(userId);
        if (!likeStorage.findUserIdsByFilmId(filmId).contains(userId)) {
            log.warn("Attempt to remove non-existing like: filmId={}, userId={}", filmId, userId);
        }
        likeStorage.delete(filmId, userId);
        log.debug("Like removed successfully for filmId={}, userId={}", filmId, userId);
    }
}