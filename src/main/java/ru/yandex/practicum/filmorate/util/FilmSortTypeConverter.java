package ru.yandex.practicum.filmorate.util;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FilmSortType;

@Component
public class FilmSortTypeConverter implements Converter<String, FilmSortType> {

    @Override
    public FilmSortType convert(String source) {
        return FilmSortType.valueOf(source.toUpperCase());
    }
}
