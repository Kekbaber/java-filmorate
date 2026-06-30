package ru.yandex.practicum.filmorate.dto.response;

import lombok.Data;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;

@Data
public class EventResponse {
    private long eventId;
    private long userId;
    private long entityId;
    private EventType eventType;
    private EventOperation operation;
    private long timestamp;
}
