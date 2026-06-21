package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.model.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MpaServiceImpl implements MpaService {

    private final MpaStorage mpaStorage;

    @Override
    public List<Mpa> findAll() {
        log.debug("Get all MPA ratings");
        return mpaStorage.findAll();
    }

    @Override
    public Mpa findById(long id) {
        log.debug("Get MPA rating by id={}", id);
        return mpaStorage.findById(id).orElseThrow(() -> new NotFoundException("Рейтинг с id=" + id + " не найден"));
    }

    @Override
    public Map<Long, Mpa> findAllByIds(Set<Long> ids) {
        log.debug("find MPAs by ids={}", ids);
        return mpaStorage.findAllByIds(ids);
    }
}