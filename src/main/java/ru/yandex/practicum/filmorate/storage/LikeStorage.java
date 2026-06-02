package ru.yandex.practicum.filmorate.storage;

import java.util.Set;

public interface LikeStorage {
    Set<Long> findUserIdsByFilmId(long filmId);

    void addLike(long filmId, long userId);

    void delete(long filmId, long userId);
}