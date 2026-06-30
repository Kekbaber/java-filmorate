package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Event {
    private long eventId;
    private long userId;
    private long entityId;
    private EventType eventType;
    private EventOperation operation;
    private long timestamp;

    public static Event of(long userId, long entityId, EventType eventType, EventOperation operation) {
        Event event = new Event();
        event.setUserId(userId);
        event.setEntityId(entityId);
        event.setEventType(eventType);
        event.setOperation(operation);
        event.setTimestamp(System.currentTimeMillis());
        return event;
    }
}
