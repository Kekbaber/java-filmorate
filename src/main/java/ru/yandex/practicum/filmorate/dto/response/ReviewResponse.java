package ru.yandex.practicum.filmorate.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ReviewResponse {
    private long reviewId;
    private String content;
    @JsonProperty("isPositive")
    private boolean isPositive;
    private long userId;
    private long filmId;
    private int useful;
}
