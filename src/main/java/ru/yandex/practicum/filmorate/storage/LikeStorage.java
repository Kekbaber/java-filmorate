package ru.yandex.practicum.filmorate.storage;

import java.util.Collection;
import java.util.Set;

public interface LikeStorage {
    public Set<Long> findById(long filmId);

    public void add(long filmId, long userId);

    public void remove(long filmId, long userId);

    Collection<Long> getMostLikedFilmIds(long limit);
}
