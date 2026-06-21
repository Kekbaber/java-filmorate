package ru.yandex.practicum.filmorate.storage.db.queries;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MpaQueries {

    public static final String FIND_ALL = "SELECT * FROM ratings order by id";

    public static final String FIND_BY_ID = "SELECT * FROM ratings where id = ?";

    public static final String FIND_BY_IDS = "SELECT * FROM ratings WHERE id IN (:ids)";
}