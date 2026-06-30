package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.dto.request.CreateFilmRequest;
import ru.yandex.practicum.filmorate.dto.request.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.dto.response.FilmResponse;
import ru.yandex.practicum.filmorate.model.FilmSortType;

import java.util.List;

public interface FilmService {
    List<FilmResponse> findAll();

    FilmResponse findById(long id);

    FilmResponse create(CreateFilmRequest request);

    FilmResponse update(UpdateFilmRequest request);

    void delete(long id);

    List<FilmResponse> findPopularFilms(long limit, Long genreId, Integer year);

    List<FilmResponse> getCommonFilms(long userId, long friendId);

    List<FilmResponse> findDirectorFilms(long directorId, FilmSortType sortType);
}
