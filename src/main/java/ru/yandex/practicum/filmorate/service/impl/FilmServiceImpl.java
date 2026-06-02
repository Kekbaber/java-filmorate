package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dto.request.CreateFilmRequest;
import ru.yandex.practicum.filmorate.dto.request.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.dto.response.FilmResponse;
import ru.yandex.practicum.filmorate.exception.model.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {

    private final FilmStorage filmStorage;
    private final FilmMapper filmMapper;
    private final GenreService genreService;
    private final MpaService mpaService;

    @Override
    public List<FilmResponse> findAll() {
        log.debug("Find all films");
        List<Film> films = filmStorage.findAll();
        log.debug("Found {} films", films.size());
        return films.stream()
                .map(this::buildFilmResponse)
                .toList();
    }

    @Override
    public FilmResponse findById(long id) {
        log.debug("Find film by id={}", id);
        return filmStorage.findById(id).map(this::buildFilmResponse)
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + id + " не найден"));
    }

    @Override
    @Transactional
    public FilmResponse create(CreateFilmRequest request) {
        log.info("Create film: name={}, releaseDate={}", request.getName(), request.getReleaseDate());
        if (request.getMpa() != null) {
            mpaService.findById(request.getMpa().getId());
        }
        Film film = filmMapper.toEntity(request);
        Film created = filmStorage.create(film);
        genreService.updateFilmGenres(film.getId(), request.getGenres());
        log.info("Created film with id={}", created.getId());
        return buildFilmResponse(created);
    }

    @Override
    @Transactional
    public FilmResponse update(UpdateFilmRequest request) {
        log.info("Update film id={}, name={}", request.getId(), request.getName());
        findById(request.getId());
        Film film = filmMapper.toEntity(request);
        Film updated = filmStorage.update(film);
        genreService.updateFilmGenres(film.getId(), request.getGenres());
        log.info("Updated film id={}", updated.getId());
        return buildFilmResponse(updated);
    }

    @Override
    public void delete(long id) {
        log.info("Delete film id={}", id);
        findById(id);
        filmStorage.delete(id);
        log.info("Deleted film id={}", id);
    }

    @Override
    public List<FilmResponse> findPopularFilms(long limit) {
        List<FilmResponse> responses = filmStorage.findPopularFilms(limit).stream()
                .map(this::buildFilmResponse)
                .toList();
        log.info("Found {} films", responses.size());
        return responses;
    }

    private FilmResponse buildFilmResponse(Film film) {
        FilmResponse response = filmMapper.toResponse(film);
        long filmId = film.getId();
        long mpaId = film.getMpaId();
        response.setMpa(mpaService.findById(mpaId));
        response.setGenres(genreService.findGenresForFilm(filmId));
        return response;
    }
}