package ru.yandex.practicum.filmorate.storage.inmemory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Profile("inmemory")
@Slf4j
public class InMemoryLikeStorage implements LikeStorage {

    private final Set<Like> likes = new HashSet<>();

    @Override
    public Set<Long> findUserIdsByFilmId(long filmId) {
        log.debug("Find likes by filmId={}", filmId);
        return likes.stream()
                .filter(like -> like.getFilmId() == filmId)
                .map(Like::getUserId)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public void addLike(long filmId, long userId) {
        likes.add(new Like(filmId, userId));
        log.debug("Storage: Add like: filmId={}, userId={}", filmId, userId);
    }

    @Override
    public void delete(long filmId, long userId) {
        log.debug("Storage: Remove like: filmId={}, userId={}", filmId, userId);
        likes.remove(new Like(filmId, userId));
    }
}
