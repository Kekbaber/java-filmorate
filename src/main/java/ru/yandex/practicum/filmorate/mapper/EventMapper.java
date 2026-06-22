package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.response.EventResponse;
import ru.yandex.practicum.filmorate.model.Event;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventMapper {

    public static EventResponse toResponse(Event event) {
        EventResponse response = new EventResponse();
        response.setEventId(event.getEventId());
        response.setUserId(event.getUserId());
        response.setEntityId(event.getEntityId());
        response.setEventType(event.getEventType());
        response.setOperation(event.getOperation());
        response.setTimestamp(event.getTimestamp());
        return response;
    }
}
