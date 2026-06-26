package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.model.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.db.queries.ReviewQueries;

import java.util.List;
import java.util.Optional;

@Repository
@Profile("database")
@Slf4j
public class ReviewDBStorage extends BaseStorage<Review> implements ReviewStorage {

    public ReviewDBStorage(JdbcTemplate jdbc, RowMapper<Review> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Review> findAll(long count) {
        log.debug("DB: find all reviews, count={}", count);
        return findMany(ReviewQueries.FIND_ALL, count);
    }

    @Override
    public Optional<Review> findById(long id) {
        log.debug("DB: find review by id={}", id);
        return findOne(ReviewQueries.FIND_BY_ID, id);
    }

    @Override
    public List<Review> findByFilmId(long filmId, long count) {
        log.debug("DB: find reviews by filmId={}, count={}", filmId, count);
        return findMany(ReviewQueries.FIND_BY_FILM_ID, filmId, count);
    }

    @Override
    public Review create(Review review) {
        long id = insert(ReviewQueries.SAVE, review.getContent(), review.isPositive(),
                review.getUserId(), review.getFilmId());
        log.debug("DB: created review id={}", id);
        return findById(id).orElseThrow();
    }

    @Override
    public Review update(Review review) {
        update(ReviewQueries.UPDATE, review.getContent(), review.isPositive(),
                review.getUserId(), review.getFilmId(), review.getId());
        log.debug("DB: updated review id={}", review.getId());
        return findById(review.getId()).orElseThrow();
    }

    @Override
    public Review delete(long id) {
        Review deleted = findById(id).get();
        delete(ReviewQueries.DELETE, id);
        log.debug("DB: deleted review id={}", id);
        return deleted;
    }

    @Override
    public void addReaction(long reviewId, long userId, boolean isLike) {
        log.debug("DB: add reaction reviewId={}, userId={}, isLike={}", reviewId, userId, isLike);
        executeUpdate(ReviewQueries.CREATE_REACTION, reviewId, userId, isLike);
    }

    @Override
    public void deleteReaction(long reviewId, long userId) {
        log.debug("DB: delete reaction reviewId={}, userId={}", reviewId, userId);
        delete(ReviewQueries.DELETE_REACTION, reviewId, userId);
    }

    @Override
    public void deleteReactionByType(long reviewId, long userId, boolean isLike) {
        log.debug("DB: delete recation by type reviewId={}, userId={}, isLike={}", reviewId, userId, isLike);
        delete(ReviewQueries.DELETE_REACTION_BY_TYPE, reviewId, userId, isLike);
    }
}