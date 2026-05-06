package ru.yandex.practicum.filmorate.util;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class FilmIdGenerator implements IdGenerator {

    private final AtomicLong counter = new AtomicLong(1);

    @Override
    public long getNextId() {
        return counter.getAndIncrement();
    }
}
