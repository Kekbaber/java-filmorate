package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.dto.request.CreateDirectorRequest;
import ru.yandex.practicum.filmorate.dto.request.UpdateDirectorRequest;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DirectorService {
    List<Director> findAll();

    Director findById(long id);

    Director create(CreateDirectorRequest request);

    Director update(UpdateDirectorRequest request);

    void delete(long id);

    void updateFilmDirectors(long filmId, List<Long> directorIds);

    Map<Long, List<Director>> findDirectorsByFilmIds(Set<Long> ids);
}
