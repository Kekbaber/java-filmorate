package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage {
    List<Review> findAll(long count);

    Optional<Review> findById(long id);

    List<Review> findByFilmId(long filmId, long count);

    Review create(Review review);

    Review update(Review review);

    void delete(long id);

    void addReaction(long reviewId, long userId, boolean isLike);

    void deleteReaction(long reviewId, long userId);

    void deleteReactionByType(long reviewId, long userId, boolean isLike);
}
