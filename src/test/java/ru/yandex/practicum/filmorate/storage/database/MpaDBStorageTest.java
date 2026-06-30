package ru.yandex.practicum.filmorate.storage.database;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.db.MpaDBStorage;
import ru.yandex.practicum.filmorate.storage.db.mappers.MpaRowMapper;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({MpaDBStorage.class, MpaRowMapper.class})
class MpaDBStorageTest {

    @Autowired
    private final MpaStorage mpaStorage;

    @Autowired
    private final JdbcTemplate jdbc;

    @Test
    void findAll_ShouldReturnAllMpa() {
        Collection<Mpa> mpaCollection = mpaStorage.findAll();
        assertThat(mpaCollection).hasSize(5);
    }

    @Test
    void findAll_ShouldReturnCorrectMpaNames() {
        Collection<Mpa> mpaCollection = mpaStorage.findAll();
        List<String> names = mpaCollection.stream()
                .map(Mpa::getName)
                .toList();
        assertThat(names).containsExactlyInAnyOrder("G", "PG", "PG-13", "R", "NC-17");
    }

    @Test
    void findById_WhenExists_ShouldReturnMpa() {
        Optional<Mpa> mpa = mpaStorage.findById(1);
        assertThat(mpa).isPresent();
        assertThat(mpa.get().getId()).isEqualTo(1);
        assertThat(mpa.get().getName()).isEqualTo("G");
    }

    @Test
    void findById_WhenExists_ShouldReturnCorrectMpaForEachId() {
        for (int i = 1; i <= 5; i++) {
            Optional<Mpa> mpa = mpaStorage.findById(i);
            assertThat(mpa).isPresent();
            assertThat(mpa.get().getId()).isEqualTo(i);
        }
    }

    @Test
    void findById_WhenNotExists_ShouldReturnEmpty() {
        Optional<Mpa> mpa = mpaStorage.findById(999L);
        assertThat(mpa).isEmpty();

        Optional<Mpa> negativeId = mpaStorage.findById(-1L);
        assertThat(negativeId).isEmpty();
    }

    @Test
    void findAllByIds_WhenEmptySet_ShouldReturnEmptyMap() {
        Map<Long, Mpa> result = mpaStorage.findAllByIds(Set.of());

        assertThat(result).isEmpty();
    }

    @Test
    void findAllByIds_ShouldReturnMapOfMpaById() {
        Map<Long, Mpa> result = mpaStorage.findAllByIds(Set.of(1L, 3L, 5L));

        assertThat(result).hasSize(3);
        assertThat(result.get(1L).getName()).isEqualTo("G");
        assertThat(result.get(3L).getName()).isEqualTo("PG-13");
        assertThat(result.get(5L).getName()).isEqualTo("NC-17");
    }

    @Test
    void findAllByIds_WhenSomeIdsNotExist_ShouldReturnOnlyExisting() {
        Map<Long, Mpa> result = mpaStorage.findAllByIds(Set.of(1L, 999L));

        assertThat(result).hasSize(1).containsKey(1L);
    }
}
