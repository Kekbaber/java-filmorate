package ru.yandex.practicum.filmorate.storage.inmemory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Profile("inmemory")
@Slf4j
public class InMemoryLikeStorage implements LikeStorage {

    private final Set<Like> likes = new HashSet<>();

    @Override
    public Set<Long> findById(long filmId) {
        log.debug("Find likes by filmId={}", filmId);
        return likes.stream()
                .filter(like -> like.getFilmId() == filmId)
                .map(Like::getUserId)
                .collect(Collectors.toSet());
    }

    @Override
    public void add(long filmId, long userId) {
        likes.add(new Like(filmId, userId));
        log.debug("Storage: Add like: filmId={}, userId={}", filmId, userId);
    }

    @Override
    public void remove(long filmId, long userId) {
        log.debug("Storage: Remove like: filmId={}, userId={}", filmId, userId);
        likes.remove(new Like(filmId, userId));
    }

    @Override
    public Collection<Long> getMostLikedFilmIds(long limit) {
        log.debug("Get most liked films: limit={}", limit);
        return likes.stream()
                .collect(Collectors.groupingBy(Like::getFilmId, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .toList();
    }
}
