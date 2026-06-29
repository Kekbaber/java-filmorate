package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dto.request.CreateDirectorRequest;
import ru.yandex.practicum.filmorate.dto.request.UpdateDirectorRequest;
import ru.yandex.practicum.filmorate.exception.model.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DirectorServiceImpl implements DirectorService {

    private final DirectorStorage storage;

    @Override
    public List<Director> findAll() {
        log.debug("findAll directors");
        return storage.findAll();
    }

    @Override
    public Director findById(long id) {
        log.debug("find director by id: {}", id);
        return storage.findById(id)
                .orElseThrow(() -> new NotFoundException("Директор с id=" + id + "не найден"));
    }

    @Override
    public Director create(CreateDirectorRequest request) {
        log.debug("create director with name: {}", request.getName());
        Director director = DirectorMapper.toEntity(request);
        Director created = storage.create(director);
        log.debug("created director with id: {}", created.getId());
        return created;
    }

    @Override
    public Director update(UpdateDirectorRequest request) {
        findById(request.getId());
        log.debug("update director with name: {}", request.getName());
        Director director = DirectorMapper.toEntity(request);
        Director updated = storage.update(director);
        log.debug("updated director with id: {}", updated.getId());
        return updated;
    }

    @Override
    @Transactional
    public void delete(long id) {
        log.debug("delete director with id: {}", id);
        findById(id);
        storage.delete(id);
        log.debug("deleted director with id: {}", id);
    }

    @Override
    public void updateFilmDirectors(long filmId, List<Long> directorIds) {
        if (directorIds != null) {
            log.debug("update film directors: filmId={}, directorIds={}", filmId, directorIds);
            Set<Long> existingIds = storage.findExistingIds(
                    new HashSet<>(directorIds));
            if (existingIds.size() != directorIds.size()) {
                throw new NotFoundException("Один или несколько режиссеров не найдены");
            }
            storage.addDirectorsToFilm(filmId, directorIds);
        } else {
            log.debug("update film directors: filmId={}, directorIds=null", filmId);
        }

    }

    @Override
    public Map<Long, List<Director>> findDirectorsByFilmIds(Set<Long> ids) {
        log.debug("find directors by film ids: {}", ids);
        return storage.findByFilmIds(ids);
    }
}
