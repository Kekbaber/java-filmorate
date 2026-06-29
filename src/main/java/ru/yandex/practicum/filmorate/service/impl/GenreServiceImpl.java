package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.model.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GenreServiceImpl implements GenreService {
    private final GenreStorage genreStorage;

    @Override
    public List<Genre> findAll() {
        log.debug("findAll genres");
        return genreStorage.findAll();
    }

    @Override
    public Genre findById(long id) {
        log.debug("find genre by id: {}", id);
        return genreStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Жанр с id=" + id + "не найден"));
    }

    @Override
    public List<Genre> findGenresForFilm(long filmId) {
        log.debug("find genres for film id: {}", filmId);
        return genreStorage.findGenresByFilmId(filmId);
    }

    @Override
    @Transactional
    public void updateFilmGenres(long filmId, List<Long> genreIds) {
        if (genreIds != null) {
            log.debug("update film genres: filmId={}, genreIds={}", filmId, genreIds);
            Set<Long> existingIds = genreStorage.findExistingGenreIds(
                    new HashSet<>(genreIds));
            if (existingIds.size() != genreIds.size()) {
                throw new NotFoundException("Один или несколько жанров не найдены");
            }
            genreStorage.updateFilmGenres(filmId, genreIds);
        } else {
            log.debug("update film genres: filmId={}, genres=null", filmId);
        }
    }

    @Override
    public Map<Long, List<Genre>> findGenresByFilmIds(Set<Long> filmIds) {
        log.debug("find genres by film ids: {}", filmIds);
        return genreStorage.findGenresByFilmIds(filmIds);
    }
}
