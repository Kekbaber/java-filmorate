package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import ru.yandex.practicum.filmorate.validation.AfterDate;
import ru.yandex.practicum.filmorate.validation.OnUpdate;

import java.time.LocalDate;

@Data
public class Film {

    @NotNull(groups = {OnUpdate.class}, message = "Id должен быть указан")
    Long id;

    @NotBlank(message = "Название не может быть пустым")
    String name;

    @Length(max = 200, message = "Максимальная длина описания — 200 символов")
    String description;

    @NotNull
    @AfterDate(value = "1895-12-28", message = "Дата релиза должна быть не раньше 28 декабря 1895 года")
    LocalDate releaseDate;

    @Positive
    Long duration;
}
