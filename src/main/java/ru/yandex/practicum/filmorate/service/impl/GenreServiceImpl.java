package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.model.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GenreServiceImpl implements GenreService {
    private final GenreStorage genreStorage;

    @Override
    public List<Genre> findAll() {
        return genreStorage.findAll();
    }

    @Override
    public Genre findById(long id) {
        return genreStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Жанр с id=" + id + "не найден"));
    }

    @Override
    public List<Genre> findGenresForFilm(long filmId) {
        return genreStorage.findGenresByFilmId(filmId);
    }

    @Override
    @Transactional
    public void updateFilmGenres(long filmId, List<Genre> genres) {
        List<Long> genreIds = null;
        if (genres != null) {
            genreIds = genres.stream()
                    .map(Genre::getId)
                    .distinct()
                    .toList();
            for (Long id : genreIds) {
                if (genreStorage.findById(id).isEmpty()) {
                    throw new NotFoundException("Жанр с id=" + id + " не найден");
                }
            }
        }
        genreStorage.updateFilmGenres(filmId, genreIds);
    }
}
