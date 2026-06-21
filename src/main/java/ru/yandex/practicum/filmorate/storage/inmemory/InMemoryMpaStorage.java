package ru.yandex.practicum.filmorate.storage.inmemory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Profile("inmemory")
@Slf4j
public class InMemoryMpaStorage implements MpaStorage {

    private final Map<Long, Mpa> ratings = new HashMap<>();

    public InMemoryMpaStorage() {
        initRatings();
    }

    private void initRatings() {
        // Инициализация рейтингов MPA в соответствии со schema.sql
        addRating(1L, "G");
        addRating(2L, "PG");
        addRating(3L, "PG-13");
        addRating(4L, "R");
        addRating(5L, "NC-17");
        log.debug("Initialized in-memory MPA ratings: {}", ratings.size());
    }

    private void addRating(long id, String name) {
        Mpa mpa = new Mpa();
        mpa.setId(id);
        mpa.setName(name);
        ratings.put(id, mpa);
    }

    @Override
    public List<Mpa> findAll() {
        log.debug("Get all MPA ratings from in-memory storage");
        return new ArrayList<>(ratings.values());
    }

    @Override
    public Optional<Mpa> findById(long id) {
        log.debug("Find MPA rating by id={} in in-memory storage", id);
        return Optional.ofNullable(ratings.get(id));
    }

    @Override
    public Map<Long, Mpa> findAllByIds(Set<Long> ids) {
        return ids.stream()
                .filter(ratings::containsKey)
                .collect(Collectors.toMap(id -> id, ratings::get));
    }
}
