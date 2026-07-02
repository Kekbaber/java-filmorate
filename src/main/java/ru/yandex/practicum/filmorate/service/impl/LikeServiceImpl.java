package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.service.EventService;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.LikeService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeServiceImpl implements LikeService {

    private final LikeStorage likeStorage;
    private final FilmService filmService;
    private final UserService userService;
    private final EventService eventService;

    @Override
    @Transactional
    public void addLike(long filmId, long userId) {
        log.debug("Add like: filmId={}, userId={}", filmId, userId);
        filmService.findById(filmId);
        userService.findById(userId);
        if (!likeStorage.findUserIdsByFilmId(filmId).contains(userId)) {
            likeStorage.addLike(filmId, userId);
            eventService.save(Event.of(userId, filmId, EventType.LIKE, EventOperation.ADD));
            log.debug("Like added: filmId={}, userId={}", filmId, userId);
        }
    }

    @Override
    @Transactional
    public void deleteLike(long filmId, long userId) {
        log.debug("Remove like: filmId={}, userId={}", filmId, userId);
        filmService.findById(filmId);
        userService.findById(userId);
        if (!likeStorage.findUserIdsByFilmId(filmId).contains(userId)) {
            log.warn("Attempt to remove non-existing like: filmId={}, userId={}", filmId, userId);
        }
        likeStorage.delete(filmId, userId);
        eventService.save(Event.of(userId, filmId, EventType.LIKE, EventOperation.REMOVE));
        log.debug("Like removed: filmId={}, userId={}", filmId, userId);
    }
}