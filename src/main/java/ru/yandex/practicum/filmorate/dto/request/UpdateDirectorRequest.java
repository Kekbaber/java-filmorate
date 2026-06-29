package ru.yandex.practicum.filmorate.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateDirectorRequest {
    @NotNull(message = "Id должен быть указан")
    private Long id;

    @NotBlank(message = "Имя не может быть пустым")
    private String name;
}
