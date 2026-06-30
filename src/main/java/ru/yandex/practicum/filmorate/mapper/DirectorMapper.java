package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.request.CreateDirectorRequest;
import ru.yandex.practicum.filmorate.dto.request.UpdateDirectorRequest;
import ru.yandex.practicum.filmorate.model.Director;

public final class DirectorMapper {

    public static Director toEntity(CreateDirectorRequest request) {
        Director director = new Director();
        director.setName(request.getName());
        return director;
    }

    public static Director toEntity(UpdateDirectorRequest request) {
        Director director = new Director();
        director.setId(request.getId());
        director.setName(request.getName());
        return director;
    }
}
