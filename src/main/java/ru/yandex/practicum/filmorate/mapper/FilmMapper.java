package ru.yandex.practicum.filmorate.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.request.CreateFilmRequest;
import ru.yandex.practicum.filmorate.dto.request.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.dto.response.FilmResponse;
import ru.yandex.practicum.filmorate.model.Film;

@Component
@RequiredArgsConstructor
public class FilmMapper {

    public Film toEntity(CreateFilmRequest request) {
        Film film = new Film();
        film.setName(request.getName());
        film.setDescription(request.getDescription());
        film.setReleaseDate(request.getReleaseDate());
        film.setDuration(request.getDuration());
        if (request.getMpa() != null) {
            film.setMpaId(request.getMpa().getId());
        }
        return film;
    }

    public Film toEntity(UpdateFilmRequest request) {
        Film film = new Film();
        film.setId(request.getId());
        film.setName(request.getName());
        film.setDescription(request.getDescription());
        film.setReleaseDate(request.getReleaseDate());
        film.setDuration(request.getDuration());
        if (request.getMpa() != null) {
            film.setMpaId(request.getMpa().getId());
        }
        return film;
    }

    public FilmResponse toResponse(Film film) {
        FilmResponse response = new FilmResponse();
        response.setId(film.getId());
        response.setName(film.getName());
        response.setDescription(film.getDescription());
        response.setReleaseDate(film.getReleaseDate());
        response.setDuration(film.getDuration());
        return response;
    }
}