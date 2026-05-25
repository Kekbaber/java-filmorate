package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import ru.yandex.practicum.filmorate.validation.constraint.AfterDate;
import ru.yandex.practicum.filmorate.validation.group.OnUpdate;

import java.time.LocalDate;
import java.util.List;

@Data
public class Film {

    @NotNull(groups = {OnUpdate.class}, message = "Id должен быть указан")
    private Long id;

    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @Length(max = 200, message = "Максимальная длина описания — 200 символов")
    private String description;

    @NotNull
    @AfterDate(value = "1895-12-28", message = "Дата релиза должна быть не раньше 28 декабря 1895 года")
    private LocalDate releaseDate;

    @Positive
    private Long duration;

    private List<Genres> genres;

    private Mpa mpa;
}
