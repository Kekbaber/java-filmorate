package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.request.CreateFilmRequest;
import ru.yandex.practicum.filmorate.dto.request.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.dto.response.FilmResponse;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FilmMapper {

    public static Film toEntity(CreateFilmRequest request) {
        Film film = new Film();
        film.setName(request.getName());
        film.setDescription(request.getDescription());
        film.setReleaseDate(request.getReleaseDate());
        film.setDuration(request.getDuration());
        if (request.getMpa() != null) {
            Mpa mpa = new Mpa();
            mpa.setId(request.getMpa().getId());
            film.setMpa(mpa);
        }
        return film;
    }

    public static Film toEntity(UpdateFilmRequest request) {
        Film film = new Film();
        film.setId(request.getId());
        film.setName(request.getName());
        film.setDescription(request.getDescription());
        film.setReleaseDate(request.getReleaseDate());
        film.setDuration(request.getDuration());
        if (request.getMpa() != null) {
            Mpa mpa = new Mpa();
            mpa.setId(request.getMpa().getId());
            film.setMpa(mpa);
        }
        return film;
    }

    public static FilmResponse toResponse(Film film) {
        FilmResponse response = new FilmResponse();
        response.setId(film.getId());
        response.setName(film.getName());
        response.setDescription(film.getDescription());
        response.setReleaseDate(film.getReleaseDate());
        response.setDuration(film.getDuration());
        return response;
    }
}