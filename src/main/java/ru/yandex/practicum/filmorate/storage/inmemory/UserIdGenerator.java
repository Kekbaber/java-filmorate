package ru.yandex.practicum.filmorate.storage.inmemory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.IdGenerator;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class UserIdGenerator implements IdGenerator {

    private final AtomicLong counter = new AtomicLong(1);

    @Override
    public long getNextId() {
        return counter.getAndIncrement();
    }
}
