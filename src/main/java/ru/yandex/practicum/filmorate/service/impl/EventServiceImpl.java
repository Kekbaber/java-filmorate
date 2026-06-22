package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.response.EventResponse;
import ru.yandex.practicum.filmorate.mapper.EventMapper;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.service.EventService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.EventStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventStorage eventStorage;
    private final UserService userService;

    @Override
    public List<EventResponse> findByUserId(long userId) {
        userService.findById(userId);
        return eventStorage.findByUserId(userId).stream()
                .map(EventMapper::toResponse)
                .toList();
    }

    @Override
    public void save(Event event) {
        eventStorage.save(event);
    }
}
