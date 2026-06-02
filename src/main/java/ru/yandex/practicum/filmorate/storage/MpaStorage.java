package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface MpaStorage {
    List<Mpa> findAll();

    Optional<Mpa> findById(long id);

    Map<Long, Mpa> findAllByIds(Set<Long> ids);
}