package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.OnUpdate;

import java.time.LocalDate;

@Data
public class User {

    @NotNull(groups = {OnUpdate.class}, message = "Id должен быть указан")
    Long id;

    @Email(message = "Электронная почта не может быть пустой и должна содержать символ @")
    String email;

    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "^\\S+$", message = "Логин не должен содержать пробелы")
    String login;

    String name;

    @Past(message = "Дата рождения не может быть в будущем")
    LocalDate birthday;

    public String getName() {
        return (name == null || name.isBlank()) ? login : name;
    }
}
