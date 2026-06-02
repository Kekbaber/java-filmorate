package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface MpaService {
    List<Mpa> findAll();

    Mpa findById(long id);

    Map<Long, Mpa> findAllByIds(Set<Long> ids);
}
