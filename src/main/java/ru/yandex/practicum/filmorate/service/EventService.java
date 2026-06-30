package ru.yandex.practicum.filmorate.service;


import ru.yandex.practicum.filmorate.dto.response.EventResponse;
import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface EventService {
    List<EventResponse> findByUserId(long userId);

    void save(Event event);
}
