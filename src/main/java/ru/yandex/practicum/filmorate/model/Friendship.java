package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Friendship {
    private final long userId;
    private final long friendId;
    private final boolean isConfirmed;
}
