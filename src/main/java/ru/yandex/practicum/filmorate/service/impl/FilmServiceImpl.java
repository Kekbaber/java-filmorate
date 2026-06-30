package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dto.request.CreateFilmRequest;
import ru.yandex.practicum.filmorate.dto.request.GenreDto;
import ru.yandex.practicum.filmorate.dto.request.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.dto.response.FilmResponse;
import ru.yandex.practicum.filmorate.exception.model.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FilmServiceImpl implements FilmService {

    private final FilmStorage filmStorage;
    private final GenreService genreService;
    private final MpaService mpaService;
    private final UserService userService;

    @Override
    public List<FilmResponse> findAll() {
        log.debug("Find all films");
        List<Film> films = filmStorage.findAll();
        log.debug("Found {} films", films.size());
        return buildFilmResponses(films);
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
        log.debug("Create film: name={}, releaseDate={}", request.getName(), request.getReleaseDate());
        if (request.getMpa() != null) {
            mpaService.findById(request.getMpa().getId());
        }
        Film film = FilmMapper.toEntity(request);
        Film created = filmStorage.create(film);
        List<Long> genreIds = request.getGenres() != null
                ? request.getGenres().stream().map(GenreDto::getId).distinct().toList()
                : null;
        genreService.updateFilmGenres(film.getId(), genreIds);
        log.debug("Created film with id={}", created.getId());
        return buildFilmResponse(created);
    }

    @Override
    @Transactional
    public FilmResponse update(UpdateFilmRequest request) {
        log.debug("Update film id={}, name={}", request.getId(), request.getName());
        findById(request.getId());
        Film film = FilmMapper.toEntity(request);
        Film updated = filmStorage.update(film);
        List<Long> genreIds = request.getGenres() != null
                ? request.getGenres().stream().map(GenreDto::getId).distinct().toList()
                : null;
        genreService.updateFilmGenres(film.getId(), genreIds);
        log.debug("Updated film id={}", updated.getId());
        return buildFilmResponse(updated);
    }

    @Override
    @Transactional
    public void delete(long id) {
        log.debug("Delete film id={}", id);
        findById(id);
        filmStorage.delete(id);
        log.debug("Deleted film id={}", id);
    }

    @Override
    public List<FilmResponse> findPopularFilms(long limit, Long genreId, Integer year) {
        if (genreId != null) {
            genreService.findById(genreId);
        }
        List<Film> films = filmStorage.findPopularFilms(limit, genreId, year);
        log.debug("Found {} films", films.size());
        return buildFilmResponses(films);
    }

    @Override
    public List<FilmResponse> getCommonFilms(long userId, long friendId) {
        userService.findById(userId);
        userService.findById(friendId);

        List<Film> films = filmStorage.findCommonFilms(userId, friendId);
        log.debug("Found {} common films", films.size());
        return buildFilmResponses(films);
    }

    private FilmResponse buildFilmResponse(Film film) {
        return buildFilmResponses(List.of(film)).getFirst();
    }

    private List<FilmResponse> buildFilmResponses(List<Film> films) {
        if (films.isEmpty()) {
            return List.of();
        }

        Set<Long> filmIds = films.stream().map(Film::getId).collect(Collectors.toSet());
        Map<Long, List<Genre>> genresMap = genreService.findGenresByFilmIds(filmIds);

        return films.stream()
                .map(film -> {
                    FilmResponse response = FilmMapper.toResponse(film);
                    response.setGenres(genresMap.getOrDefault(film.getId(), List.of()));
                    response.setMpa(film.getMpa());
                    return response;
                })
                .toList();
    }
}