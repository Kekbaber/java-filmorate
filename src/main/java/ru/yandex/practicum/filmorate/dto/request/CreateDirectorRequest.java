package ru.yandex.practicum.filmorate.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateDirectorRequest {
    @NotBlank(message = "Имя не может быть пустым")
    private String name;
}
