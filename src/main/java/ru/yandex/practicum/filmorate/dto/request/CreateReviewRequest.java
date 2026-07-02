package ru.yandex.practicum.filmorate.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateReviewRequest {
    @NotBlank
    private String content;
    @NotNull
    @JsonProperty("isPositive")
    private Boolean positive;
    @NotNull
    private Long userId;
    @NotNull
    private Long filmId;
}