package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.model.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MpaServiceImpl implements MpaService {

    private final MpaStorage mpaStorage;

    @Override
    public List<Mpa> findAll() {
        return mpaStorage.findAll();
    }

    @Override
    public Mpa findById(long id) {
        return mpaStorage.findById(id).orElseThrow(() -> new NotFoundException("Рейтинг с id=" + id + " не найден"));
    }
}